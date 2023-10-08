package com.example.shaden.features.authentication;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping(value = "/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        LOGGER.info("Login controller endpoint called.");
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) throws IOException {
        AuthenticationResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

}
