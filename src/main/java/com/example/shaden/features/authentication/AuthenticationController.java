package com.example.shaden.features.authentication;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.exception.custom.UnauthorizedException;
import com.example.shaden.features.ResponseData;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.authentication.response.AuthenticationResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping(value = "/register")
    public ResponseEntity<ResponseData> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authService.register(request, response);
        
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.CREATED.value());
        responseData.setMessage("User registered successfully");
        responseData.setResults(authResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ResponseData> login(@Valid @RequestBody AuthenticationRequest request, HttpServletResponse response) {
        LOGGER.info("Login controller endpoint called.");
        AuthenticationResponse authResponse = authService.authenticate(request, response);

        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("User authenticated successfully");
        responseData.setResults(authResponse);

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<ResponseData> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) throws IOException {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new UnauthorizedException("Refresh token is missing");
        }

        AuthenticationResponse authResponse = authService.refreshToken(refreshToken, response);
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("Token refreshed successfully");
        responseData.setResults(authResponse);

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

}
