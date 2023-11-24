package com.example.shaden.features.friend;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.features.friend.response.FriendResponse;
import com.example.shaden.features.friend.response.PendingFriendRequestResponse;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserPrincipal;
import com.example.shaden.features.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {
    
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    private Logger LOG = LoggerFactory.getLogger(FriendService.class.getName());

    public void sentFriendRequest(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        
        if (user.getUser().getUsername().equals(username)) {
            LOG.info(username + " is trying to add himself as a friend.");
            throw new IllegalArgumentException("You cannot add yourself as a friend.");
        }
    
        User friend = userRepository.findByUsername(username);

        if (friend == null) {
            throw new ResourceNotFoundException("User not found");
        }
    
        Friendship existingFriendship = friendRepository.findBySenderAndReceiver(user.getUser(), friend);
    
        if (existingFriendship != null) {
            switch (existingFriendship.getStatus()) {
                case ACCEPTED:
                    throw new IllegalArgumentException("Friendship already exists");
                case PENDING:
                    throw new IllegalArgumentException("Friendship is pending");
                case REJECTED:
                    friendRepository.updateFriendShipStatus(existingFriendship.getId(), FriendshipStatus.PENDING);
                    break;
                case BLOCKED:
                    throw new IllegalArgumentException("Friendship is blocked by a user");
                default:
                    break;
            }
        } else {
            if (auth != null && auth.isAuthenticated()) {
                Friendship newFriendship = Friendship.builder()
                        .sender(user.getUser())
                        .receiver(friend)
                        .status(FriendshipStatus.PENDING)
                        .build();
        
                friendRepository.save(newFriendship);
            }
        }
    }

    public List<FriendResponse> getAllFriends() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
    
        List<Friendship> friendships = friendRepository.findAllFriendsByStatus(user.getUser(), FriendshipStatus.ACCEPTED);
    
        List<FriendResponse> friendResponses = friendships.stream()
            .map(friendship -> {
                User friend = friendship.getReceiver().equals(user.getUser()) ? friendship.getSender() : friendship.getReceiver();
    
                return new FriendResponse(friend.getId(), friend.getUsername(), friendship.getStatus());
            })
            .collect(Collectors.toList());
    
        return friendResponses;
    }

    public FriendResponse getFriendById(Long friendId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
    
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));
    
        Friendship friendship = friendRepository.findFriendByOrId(user.getUser(), friendId);
    
        if (friendship == null) {
            throw new ResourceNotFoundException("Friendship not found");
        }
        
        switch (friendship.getStatus()) {
            case PENDING:
                throw new IllegalArgumentException("Friendship is pending");
            case REJECTED:
                throw new IllegalArgumentException("Friendship is rejected");
            case BLOCKED:
                throw new IllegalArgumentException("Friendship is blocked");
            default:
                break;
        }
    
        return new FriendResponse(friend.getId(), friend.getUsername(), friendship.getStatus());
    }

    public void acceptFriend(Long friendId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
    
        User receiver = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));
    
        Friendship outgoingFriendship = friendRepository.findBySenderAndReceiver(currentUser.getUser(), receiver);
        Friendship incomingFriendship = friendRepository.findBySenderAndReceiver(receiver, currentUser.getUser());
    
        if (outgoingFriendship != null) {
            acceptFriendship(outgoingFriendship, currentUser);
        } else if (incomingFriendship != null) {
            acceptFriendship(incomingFriendship, currentUser);
        } else {
            throw new ResourceNotFoundException("Friendship not found");
        }
    }
    
    private void acceptFriendship(Friendship friendship, UserPrincipal currentUser) {
        if (friendship.getSender().equals(currentUser.getUser())) {
            throw new IllegalArgumentException("Sender cannot accept their own friend request");
        }
    
        friendRepository.updateFriendShipStatus(friendship.getId(), FriendshipStatus.ACCEPTED);
    }

    public List<PendingFriendRequestResponse> getPendingFriendRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    
        List<Friendship> friendships = friendRepository.findAllBySenderAndStatusOrReceiverAndStatus(
            userPrincipal.getUser(), FriendshipStatus.PENDING, userPrincipal.getUser(), FriendshipStatus.PENDING);
    
        List<PendingFriendRequestResponse> pendingFriendRequestResponses = friendships.stream()
            .map(friendship -> {
                User friend = (friendship.getSender().getId().equals(userPrincipal.getUser().getId())) ? friendship.getReceiver() : friendship.getSender();
                String requestType = (friendship.getSender().getId().equals(userPrincipal.getUser().getId())) ? "OUTGOING" : "INCOMING";
                
                return PendingFriendRequestResponse.builder()
                    .requestId(friendship.getId())
                    .friendId(friend.getId())
                    .friendUsername(friend.getUsername())
                    .status(friendship.getStatus().toString())
                    .requestType(requestType)
                    .build();
            })
            .collect(Collectors.toList());
    
        return pendingFriendRequestResponses;
    }
    
    
    public void rejectFriend(Long friendId) {
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        
            User friend = userRepository.findById(friendId)
                    .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));
        
            Friendship friendship = friendRepository.findBySenderAndReceiver(friend, user.getUser());
        
            if (friendship == null) {
                throw new ResourceNotFoundException("Friendship not found");
            }

            switch (friendship.getStatus()) {
                case PENDING:
                    break;
                case REJECTED:
                    throw new IllegalArgumentException("Friendship is already rejected");
                case ACCEPTED:
                    throw new IllegalArgumentException("Friendship is already accepted");
                case BLOCKED:
                    throw new IllegalArgumentException("Friendship is blocked");
                default:
                    break;
            }
        
            friendRepository.updateFriendShipStatus(friendship.getId(), FriendshipStatus.REJECTED);
    }
    
    public void cancelOutgoingFriendRequest(Long friendId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
    
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));
    
        Friendship friendship = friendRepository.findBySenderAndReceiver(user.getUser(), friend);
    
        if (friendship == null) {
            throw new ResourceNotFoundException("Friendship not found");
        }
        
        switch (friendship.getStatus()) {
            case PENDING:
                break;
            case REJECTED:
                throw new IllegalArgumentException("Friendship is already rejected");
            case ACCEPTED:
                throw new IllegalArgumentException("Friendship is already accepted");
            case BLOCKED:
                throw new IllegalArgumentException("Friendship is blocked");
            default:
                break;
        }
    
        friendRepository.delete(friendship);

    }

    public void removeFriend(Long friendId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
    
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));
    
        Friendship friendship = friendRepository.findBySenderAndReceiver(user.getUser(), friend);
    
        if (friendship == null) {
            throw new ResourceNotFoundException("Friendship not found");
        }
        
        friendRepository.delete(friendship);
    }

}
