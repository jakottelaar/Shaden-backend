package com.example.shaden.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import com.example.shaden.features.ResponseData;
import com.example.shaden.features.authentication.AuthenticationController;
import com.example.shaden.features.authentication.AuthenticationService;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.authentication.response.AuthenticationResponse;

import jakarta.servlet.http.HttpServletResponse;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthControllerUnitTests {
    
    @InjectMocks
    private AuthenticationController authController;

    @Mock
    private AuthenticationService authService;
    
    @Test
    public void Register_user_endpoint() {

        RegisterRequest registerRequest = RegisterRequest.builder()
            .username("test")
            .email("test@mail.com")
            .password("Testpass1234")
            .build();

        AuthenticationResponse authResponse = new AuthenticationResponse("token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authService.register(any(RegisterRequest.class), any(HttpServletResponse.class))).thenReturn(authResponse);

        ResponseEntity<?> responseEntity = authController.register(registerRequest, response);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void Login_user_endpoint() {

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
        .email("test@mail.com")
        .password("Testpass1234")
        .build();

        AuthenticationResponse authResponse = new AuthenticationResponse("token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authService.authenticate(any(AuthenticationRequest.class), any(HttpServletResponse.class)))
            .thenReturn(authResponse);

        ResponseEntity<ResponseData> responseEntity = authController.login(authRequest, response);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void Refresh_token_endpoint() throws IOException {
        AuthenticationResponse authResponse = new AuthenticationResponse("token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        String validRefreshToken = "validRefreshToken";

        when(authService.refreshToken(validRefreshToken, response)).thenReturn(authResponse);

        ResponseEntity<ResponseData> responseEntity = authController.refreshToken(validRefreshToken, response);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    
}
