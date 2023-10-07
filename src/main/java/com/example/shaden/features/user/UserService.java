package com.example.shaden.features.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    
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

        Optional<User> user = userRepository.findById(userId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Create global catch for non authenticated user or admin
        if (user.isPresent() && auth != null && auth.isAuthenticated() && user.get().getEmail().equals(auth.getName())) {
            userRepository.delete(user.get());
            LOGGER.error("User successfully deleted");
        } else {
            LOGGER.error("User not found");
            throw new ResourceNotFoundException("User not found");
        }

    }

}
