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
import org.springframework.mock.web.MockHttpServletRequest;

import com.example.shaden.features.ResponseData;
import com.example.shaden.features.authentication.AuthenticationController;
import com.example.shaden.features.authentication.AuthenticationService;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.authentication.response.AuthenticationResponse;


@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTests {
    
    @InjectMocks
    private AuthenticationController authController;

    @Mock
    private AuthenticationService authService;
    
    @Test
    public void testRegisterUserEndpoint() {

        RegisterRequest registerRequest = RegisterRequest.builder()
            .username("test")
            .email("test@mail.com")
            .password("Testpass1234")
            .build();

        AuthenticationResponse authResponse = new AuthenticationResponse("token", "user");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        ResponseEntity<?> responseEntity = authController.register(registerRequest);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testLoginUserEndpoint() {

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
        .email("test@mail.com")
        .password("Testpass1234")
        .build();

        AuthenticationResponse authResponse = new AuthenticationResponse("token", "user");

        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(authResponse);

        ResponseEntity<ResponseData> responseEntity = authController.login(authRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testRefreshTokenEndpoint() throws IOException {
        AuthenticationResponse authResponse = new AuthenticationResponse("token", "user");

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(authService.refreshToken(request)).thenReturn(authResponse);

        ResponseEntity<ResponseData> responseEntity = authController.refreshToken(request);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    
}
