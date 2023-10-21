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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.shaden.config.JsonParserUtil;
import com.example.shaden.features.authentication.AuthenticationService;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    public void testGetUserProfile() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profile")
                .header("Authorization", "Bearer " + accessTokenUser1))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().equals("Successfully retrieved user profile"));
        assert(jsonResponse.get("results").get("username").asText().equals("testUser1"));
        assert(jsonResponse.get("results").get("email").asText().equals("testuser1@mail.com"));
    }

    @Test
    @Order(2)
    public void testUser2SearchUser1() throws Exception {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/search?username=testUser1")
                    .header("Authorization", "Bearer " + accessTokenUser2))
                    .andExpect(status().isOk())
                    .andReturn();

            JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

            assert(jsonResponse.get("status").asInt() == 200);
            assert(jsonResponse.get("message").asText().equals("Successfully retrieved users"));
            assert(jsonResponse.get("results").get(0).get("username").asText().equals("testUser1"));
            assert(jsonResponse.get("results").get(0).get("email").asText().equals("testuser1@mail.com"));
    }

    @Test
    @Order(2)
    public void testUser1SearchAllUsers() throws Exception {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/search?username=testUser")
                    .header("Authorization", "Bearer " + accessTokenUser2))
                    .andExpect(status().isOk())
                    .andReturn();

            JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

            assert(jsonResponse.get("status").asInt() == 200);
            assert(jsonResponse.get("message").asText().equals("Successfully retrieved users"));
            assert(jsonResponse.get("results").get(0).get("username").asText().equals("testUser1"));
            assert(jsonResponse.get("results").get(0).get("email").asText().equals("testuser1@mail.com"));
            assert(jsonResponse.get("results").get(1).get("username").asText().equals("testUser2"));
            assert(jsonResponse.get("results").get(1).get("email").asText().equals("testuser2@mail.com"));
    }

    @Test
    @Order(3)
    public void testUpdateUserProfile() throws Exception {
            
        String jsonRequest = "{\"username\":\"testUser1Changed\",\"email\":\"test1changed@mail.com\"}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/profile")
                .header("Authorization", "Bearer " + accessTokenUser1)
                .contentType("application/json")
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
        
        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().equals("Successfully updated user profile"));
        assert(jsonResponse.get("results").get("username").asText().equals("testUser1Changed"));
        assert(jsonResponse.get("results").get("email").asText().equals("test1changed@mail.com"));

    }

    @Test
    @Order(4)
    public void testDeleteUserAccount() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/profile")
                .header("Authorization", "Bearer " + accessTokenUser1))
                .andExpect(status().isNoContent())
                .andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 204);
        assert(jsonResponse.get("message").asText().equals("Successfully deleted your account"));
        assert(jsonResponse.get("results").asText().equals("null"));
    }

}
