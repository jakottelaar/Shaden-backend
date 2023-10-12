package com.example.shaden.features.user;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.features.user.response.UserProfileResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<UserProfileResponse> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Optional<User> user = userRepository.findByEmail(userDetails.getUsername());        
            if (user.isPresent()) {
                UserProfileResponse response = UserProfileResponse.builder()
                .email(user.get().getEmail())
                .username(user.get().getUsername())
                .build();

                return Optional.of(response);
            }
        }
        
        throw new ResourceNotFoundException("User not found");
    }

    public void deleteUserWithId(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.delete(userToDelete);
    }

    public void deleteUserWithEmail() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Optional<User> user = userRepository.findByEmail(userDetails.getUsername());        
            if (user.isPresent()) {
                userRepository.delete(user.get());
                return;
            }
        }
        
        throw new ResourceNotFoundException("User not found");

    }

}
