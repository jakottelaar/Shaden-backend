package com.example.shaden.features.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.features.user.response.UserProfileResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
        private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        LOGGER.info("Get user profile endpoint called");
        return ResponseEntity.ok(userService.getProfile().get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserWithId(@PathVariable("userId") Long userId) {
        LOGGER.info("Delete user endpoint called");
        userService.deleteUserWithId(userId);
        return ResponseEntity.ok().body("Successfully deleted user with id");
    }

}
