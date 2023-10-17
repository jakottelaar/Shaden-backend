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

import com.example.shaden.features.ResponseData;

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
    public ResponseEntity<ResponseData> register(@Valid @RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.register(request);
        
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.CREATED.value());
        responseData.setMessage("User registered successfully");
        responseData.setResults(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ResponseData> login(@Valid @RequestBody AuthenticationRequest request) {
        LOGGER.info("Login controller endpoint called.");
        AuthenticationResponse response = authService.authenticate(request);

        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.CREATED.value());
        responseData.setMessage("User authenticated successfully");
        responseData.setResults(response);

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<ResponseData> refreshToken(HttpServletRequest request) throws IOException {
        AuthenticationResponse response = authService.refreshToken(request);
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.CREATED.value());
        responseData.setMessage("Token refreshed successfully");
        responseData.setResults(response);

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

}
