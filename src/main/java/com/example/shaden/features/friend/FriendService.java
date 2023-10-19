package com.example.shaden.features.friend;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.features.friend.response.FriendResponse;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserPrincipal;
import com.example.shaden.features.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {
    
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public void addFriend(Long friendId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
    
        if (user.getUserId().equals(friendId)) {
            throw new IllegalArgumentException("You cannot add yourself as a friend.");
        }
    
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));
    
        Friendship existingFriendship = friendRepository.findByFriend1AndFriend2(user.getUser(), friend);
    
        if (existingFriendship != null) {
            throw new IllegalArgumentException("Friendship already established.");
        }
    
        if (auth != null && auth.isAuthenticated()) {
            Friendship newFriendship = Friendship.builder()
                    .friend1(user.getUser())
                    .friend2(friend)
                    .status(FriendshipStatus.PENDING)
                    .build();
    
            friendRepository.save(newFriendship);
        }
    }

    public List<FriendResponse> getAllFriends() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();

        List<Friendship> friendships = friendRepository.findAllByFriend1OrFriend2(user.getUser(), user.getUser());

        List<FriendResponse> friendResponses = friendships.stream()
            .map(friendship -> {
                User friend = friendship.getFriend1().equals(user.getUser()) ? friendship.getFriend2() : friendship.getFriend1();

                return new FriendResponse(friend.getId(), friend.getUsername());
            })
            .collect(Collectors.toList());

        return friendResponses;
    }

    public FriendResponse getFriendById(Long friendId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
    
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));
    
        Friendship friendship = friendRepository.findByFriend1AndFriend2(user.getUser(), friend);
    
        if (friendship == null) {
            throw new ResourceNotFoundException("Friendship not found");
        }
    
        return new FriendResponse(friend.getId(), friend.getUsername());
    }

}
