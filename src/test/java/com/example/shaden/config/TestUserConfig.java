package com.example.shaden.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.example.shaden.features.authentication.AuthenticationService;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserRepository;

import jakarta.annotation.PostConstruct;

@Configuration
@ActiveProfiles("test")
public class TestUserConfig {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUserConfig.class);

    private static String testUserToken1;
    private static String testUserToken2;
    private static String testUserToken3;
    private static User testUser1;
    private static User testUser2;
    private static User testUser3;

    @PostConstruct
    public void setup() {
        RegisterRequest testUser1Request = new RegisterRequest("testUser1", "testUser1@mail.com", "testUser1");
        RegisterRequest testUser2Request = new RegisterRequest("testUser2", "testUser2@mail.com", "testUser2");
        RegisterRequest testUser3Request = new RegisterRequest("testUser3", "testUser3@mail.com", "testUser3");

        authenticationService.register(testUser1Request);
        authenticationService.register(testUser2Request);
        authenticationService.register(testUser3Request);

        try {
            testUserToken1 = obtainAccessToken("testUser1@mail.com", "testUser1");
            testUserToken2 = obtainAccessToken("testUser2@mail.com", "testUser2");
            testUserToken3 = obtainAccessToken("testUser3@mail.com", "testUser3");
            testUser1 = userRepository.findByEmail("testUser1@mail.com").orElse(null);
            testUser2 = userRepository.findByEmail("testUser2@mail.com").orElse(null);
            testUser3 = userRepository.findByEmail("testUser3@mail.com").orElse(null);

        } catch (Exception e) {
            LOGGER.error("Error setting up test users: " + e.getMessage());
        }
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        String token = authenticationService.authenticate(authenticationRequest).getAccessToken();
        return token;
    }


    @Bean
    String getTestUserToken1() {
        return testUserToken1;
    }

    @Bean
    String getTestUserToken2() {
        return testUserToken2;
    }

    @Bean
    String getTestUserToken3() {
        return testUserToken3;
    }

    @Bean
    User getTestUser1() {
        return testUser1;
    }

    @Bean
    User getTestUser2() {
        return testUser2;
    }

    @Bean
    User getTestUser3() {
        return testUser3;
    }
}

