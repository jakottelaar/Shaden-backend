package com.example.shaden.authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.example.shaden.exception.custom.DuplicateDataException;
import com.example.shaden.features.authentication.AuthenticationService;
import com.example.shaden.features.authentication.JwtService;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.authentication.response.AuthenticationResponse;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthServiceUnitTests {
    
    @InjectMocks
    private AuthenticationService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterUser() {
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("test")
                .email("test@mail.com")
                .password("Testpass1234")
                .build();

        authService.register(registerRequest);

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode("Testpass1234");

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void testDuplicateEmail() {
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("test")
                .email("existing@mail.com")
                .password("Testpass1234")
                .build();

        assertThrows(DuplicateDataException.class, () -> authService.register(registerRequest));
    }

    @Test
    public void testValidRegistration() {
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("test")
                .email("test@mail.com")
                .password("Testpass1234")
                .build();

        AuthenticationResponse response = authService.register(registerRequest);

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode("Testpass1234");

        assertNotNull(response);
    }


}
