package com.example.shaden.features.authentication;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shaden.exception.custom.DuplicateDataException;
import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.exception.custom.UnauthorizedException;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.authentication.response.AuthenticationResponse;
import com.example.shaden.features.user.Role;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserPrincipal;
import com.example.shaden.features.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationResponse register(RegisterRequest request) {
        LOGGER.info("Registering user");

        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateDataException("Email already exists");
        }

        if (repository.existsByUsername(request.getUsername())) {
            throw new DuplicateDataException("Username already exists");
        }

        var user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        
        repository.save(user);

        UserPrincipal userPrincipal = UserPrincipal.builder().user(user).build();

        var jwtToken = jwtService.generateToken(userPrincipal, Optional.of(user));
        var refreshToken = jwtService.generateRefreshToken(userPrincipal, Optional.of(user));
        return AuthenticationResponse
        .builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        LOGGER.info("Authenticating user");
    
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        }

        var user = repository.findByEmail(request.getEmail());
    
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("No user found with email: " + request.getEmail());
        }

        UserPrincipal userPrincipal = UserPrincipal.builder().user(user.get()).build();

        var jwtToken = jwtService.generateToken(userPrincipal, Optional.of(user.get()));
        var refreshToken = jwtService.generateRefreshToken(userPrincipal, Optional.of(user.get()));
        return AuthenticationResponse
        .builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        LOGGER.info("Refreshing token");
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final Long userId;
    
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResourceNotFoundException("No authorization header or refresh token");
        }
    
        refreshToken = authorizationHeader.substring(7);
        userId = jwtService.extractUserId(refreshToken);
    
        if (userId != null) {
            var user = this.repository.findById(userId)
                    .orElseThrow();

            UserPrincipal userPrincipal = UserPrincipal.builder().user(user).build();

            if (jwtService.isTokenValid(refreshToken, userPrincipal)) {
                var accessToken = jwtService.generateToken(userPrincipal, Optional.of(user));
                var newRefreshToken = jwtService.generateRefreshToken(userPrincipal, Optional.of(user));
                
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            }
        }
        
        throw new ResourceNotFoundException("Invalid refresh token");
    }
    
 
}
