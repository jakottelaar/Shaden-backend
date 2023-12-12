package com.example.shaden.features.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.features.user.request.UserProfileUpdateRequest;
import com.example.shaden.features.user.response.SearchUserResponse;
import com.example.shaden.features.user.response.UserProfileResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<UserProfileResponse> getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        if (auth != null && auth.isAuthenticated()) {
            Optional<User> user = userRepository.findById(userPrincipal.getUserId());
            if (user.isPresent()) {
                UserProfileResponse response = UserProfileResponse.builder()
                .userId(user.get().getId())
                .email(user.get().getEmail())
                .username(user.get().getUsername())
                .build();

                return Optional.of(response);
            }
        }
        
        throw new ResourceNotFoundException("User not found");
    }

    public List<SearchUserResponse> searchUsers(String username) {
        List<SearchUserResponse> searchUserResponse = userRepository.findByUsernameContaining(username);
        return searchUserResponse;
    }
    
    public void deleteUserWithId(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.delete(userToDelete);
    }


    public Optional<UserProfileResponse> updateUserProfile(UserProfileUpdateRequest updateRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    
        Optional<User> userOptional = userRepository.findById(userPrincipal.getUserId());

        if (userRepository.existsByEmail(updateRequest.getEmail().get()) && !userOptional.get().getEmail().equals(updateRequest.getEmail().get())) {
            throw new ResourceNotFoundException("Email already exists");
        }

        if (userRepository.existsByUsername(updateRequest.getUsername().get()) && !userOptional.get().getUsername().equals(updateRequest.getUsername().get())) {
            throw new ResourceNotFoundException("Username already exists");
        }
    
        if (userOptional.isPresent() ) {
            User user = userOptional.get();
            updateRequest.getUsername().ifPresent(user::setUsername);
            updateRequest.getEmail().ifPresent(user::setEmail);

            userRepository.save(user);

            UserProfileResponse userProfileResponse = UserProfileResponse.builder()
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();
    
            return Optional.of(userProfileResponse);
        } else {
            throw new ResourceNotFoundException("User not found");
        }
    }

    public void deleteUserWithTokenId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        if (auth != null && auth.isAuthenticated()) {
            Optional<User> user = userRepository.findById(userPrincipal.getUserId());
            if (user.isPresent()) {
                userRepository.delete(user.get());
                return;
            }
        }
        
        throw new ResourceNotFoundException("User not found");

    }

}
