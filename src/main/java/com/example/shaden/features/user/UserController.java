package com.example.shaden.features.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.features.ResponseData;
import com.example.shaden.features.user.request.UserProfileUpdateRequest;
import com.example.shaden.features.user.response.UserProfileResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/profile")
    public ResponseEntity<ResponseData> getUserProfile() {
        LOGGER.info("Get user profile endpoint called");
        Optional<UserProfileResponse> userProfileResponse = userService.getUserProfile();

        ResponseData responseBody = new ResponseData();
        responseBody.setStatusCode(HttpStatus.OK.value());
        responseBody.setMessage("Successfully retrieved user profile");
        responseBody.setResults(userProfileResponse);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseData> deleteUserWithId(@PathVariable(value = "id", required = false) Long userId) {
        LOGGER.info("Delete user endpoint called");
        userService.deleteUserWithId(userId);
        ResponseData responseBody = new ResponseData();
        responseBody.setStatusCode(HttpStatus.NO_CONTENT.value());
        responseBody.setMessage("Successfully deleted user");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseBody);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<ResponseData> deleteUserWithTokenId() {
        LOGGER.info("Delete user endpoint called");
        userService.deleteUserWithTokenId();

        ResponseData responseBody = new ResponseData();
        responseBody.setStatusCode(HttpStatus.NO_CONTENT.value());
        responseBody.setMessage("Successfully deleted your account");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseBody);
    }

    @PatchMapping("/profile")
    public ResponseEntity<ResponseData> updateUserProfile(@RequestBody UserProfileUpdateRequest updateRequest) {
        LOGGER.info("Update user profile endpoint called");
        Optional<UserProfileResponse> userProfileResponse = userService.updateUserProfile(updateRequest);

        ResponseData responseBody = new ResponseData();
        responseBody.setStatusCode(HttpStatus.OK.value());
        responseBody.setMessage("Successfully updated user profile");
        responseBody.setResults(userProfileResponse);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
