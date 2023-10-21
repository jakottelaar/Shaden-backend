package com.example.shaden.users;

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

    private static String accessToken;

    @BeforeAll
    public void setup() throws Exception {
        
        RegisterRequest testUser = new RegisterRequest("testUser1","test1@mail.com", "testMan1");
        authenticationService.register(testUser);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test1@mail.com", "testMan1");

        accessToken = authenticationService.authenticate(authenticationRequest).getAccessToken();

    }

    @Test
    @Order(1)
    public void testGetUserProfile() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profile")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().equals("Successfully retrieved user profile"));
        assert(jsonResponse.get("results").get("username").asText().equals("testUser1"));
        assert(jsonResponse.get("results").get("email").asText().equals("test1@mail.com"));
    }

    @Test
    @Order(2)
    public void testUpdateUserProfile() throws Exception {
            
        String jsonRequest = "{\"username\":\"testUser1Changed\",\"email\":\"test1changed@mail.com\"}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/profile")
                .header("Authorization", "Bearer " + accessToken)
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
    @Order(2)
    public void testDeleteUserAccount() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/profile")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 204);
        assert(jsonResponse.get("message").asText().equals("Successfully deleted your account"));
        assert(jsonResponse.get("results").asText().equals("null"));
    }

}
