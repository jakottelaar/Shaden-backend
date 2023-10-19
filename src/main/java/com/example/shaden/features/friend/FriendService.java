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

}
