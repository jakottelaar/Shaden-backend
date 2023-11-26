package com.example.shaden.users;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.shaden.features.authentication.AuthenticationService;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.user.UserRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class UserIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    private static String accessTokenUser1;
    private static String accessTokenUser2;

    @BeforeAll
    public void setup() throws Exception {
        
        RegisterRequest testUser = new RegisterRequest("testUser1","testuser1@mail.com", "testMan1");
        RegisterRequest testUser2 = new RegisterRequest("testUser2","testuser2@mail.com", "testMan2");
        authenticationService.register(testUser);
        authenticationService.register(testUser2);

        AuthenticationRequest authenticationRequestUser1 = new AuthenticationRequest("testuser1@mail.com", "testMan1");
        AuthenticationRequest authenticationRequestUser2 = new AuthenticationRequest("testuser2@mail.com", "testMan2");

        accessTokenUser1 = authenticationService.authenticate(authenticationRequestUser1).getAccessToken();
        accessTokenUser2 = authenticationService.authenticate(authenticationRequestUser2).getAccessToken();

    }

    @AfterAll
    public void teardown() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void Get_user_profile() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profile")
                .header("Authorization", "Bearer " + accessTokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved user profile"))
                .andExpect(jsonPath("$.results.username").value("testUser1"))
                .andExpect(jsonPath("$.results.email").value("testuser1@mail.com"));
    }

    @Test
    @Order(2)
    public void User2_searches_user1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/search?username=testUser1")
                .header("Authorization", "Bearer " + accessTokenUser2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved users"))
                .andExpect(jsonPath("$.results[0].username").value("testUser1"))
                .andExpect(jsonPath("$.results[0].email").value("testuser1@mail.com"));
    }

    @Test
    @Order(3)
    public void User1_searches_other_users() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/search?username=testUser")
                .header("Authorization", "Bearer " + accessTokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved users"))
                .andExpect(jsonPath("$.results[0].username").value("testUser1"))
                .andExpect(jsonPath("$.results[0].email").value("testuser1@mail.com"))
                .andExpect(jsonPath("$.results[1].username").value("testUser2"))
                .andExpect(jsonPath("$.results[1].email").value("testuser2@mail.com"));
     }

     @Test
     @Order(4)
     public void Update_profile() throws Exception {
        String jsonRequest = "{\"username\":\"testUser1Changed\",\"email\":\"test1changed@mail.com\"}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/profile")
                .header("Authorization", "Bearer " + accessTokenUser1)
                .contentType("application/json")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully updated user profile"))
                .andExpect(jsonPath("$.results.username").value("testUser1Changed"))
                .andExpect(jsonPath("$.results.email").value("test1changed@mail.com"));
     }

     @Test
     @Order(5)
     public void Delete_account() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/profile")
                .header("Authorization", "Bearer " + accessTokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully deleted your account"));
     }

}