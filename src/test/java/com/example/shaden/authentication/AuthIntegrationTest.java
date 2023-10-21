package com.example.shaden.authentication;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.shaden.config.JsonParserUtil;
import com.fasterxml.jackson.databind.JsonNode;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    private static String accessToken;
    private static String refreshToken;

    //Test account registration
    @Test
    @Order(1)
    public void accountRegistrationTest() throws Exception {
        String jsonRequest = "{\"username\":\"authTestUsername\",\"email\":\"authtest@mail.com\",\"password\":\"Testpass1234\"}";
   
        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 201);
        assert(jsonResponse.get("message").asText().contains("User registered successfully"));
        assert(jsonResponse.get("results").get("access_token").asText() != null);
        assert(jsonResponse.get("results").get("refresh_token").asText() != null);
    }

    //Test invalid input for account registration
    @Test
    @Order(2)
    public void invalidInputAccountRegistration() throws Exception {
        String jsonRequest = "{\"username\":\"\",\"email\":\"testmail.com\",\"password\":\"Testpass\"}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);
        assert(jsonResponse.get("message").asText().contains("Validation failed"));
        assert(jsonResponse.get("errors").toString().contains("Username is required"));
        assert(jsonResponse.get("errors").toString().contains("Invalid email format"));
        assert(jsonResponse.get("errors").toString().contains("Password must contain at least one letter, one digit, and be 8 or more characters long"));
    }

    //Test account registration with email that already exists
    @Test
    @Order(3)
    public void accountWithEmailAlreadyExists() throws Exception {
        String jsonRequest = "{\"username\":\"authTestUsername\",\"email\":\"authtest@mail.com\",\"password\":\"Testpass1234\"}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);
        assert(jsonResponse.get("message").asText().contains("Email already exists"));
    }
    
    //Test login
    @Test
    @Order(4)
    public void testLogin() throws Exception {
        String jsonRequest = "{\"email\":\"authtest@mail.com\",\"password\":\"Testpass1234\"}";
   
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
   
        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("message").asText().contains("User authenticated successfully"));
        assert(jsonResponse.get("results").get("access_token").asText() != null);
        assert(jsonResponse.get("results").get("refresh_token").asText() != null);

        accessToken = jsonResponse.get("results").get("access_token").asText();
        refreshToken = jsonResponse.get("results").get("refresh_token").asText();
    }

    //Test invalid credentials login
    @Test
    @Order(5)
    public void testInvalidCredentialsLogin() throws Exception {

        String jsonRequest = "{\"email\":\"authtest@mail.com\",\"password\":\"Testpass1\"}";
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assert(result.getResponse().getContentAsString().contains("Invalid email or password"));
        
        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("message").asText().contains("Invalid email or password"));

    }

    //Test refresh token
    @Test
    @Order(6)
    public void testRefreshToken() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refresh-token")
                .header("Authorization", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("message").asText().contains("Token refreshed successfully"));
        assert(jsonResponse.get("results").get("access_token").asText() != null);
        assert(jsonResponse.get("results").get("refresh_token").asText() != null);

    }

    //Test delete registered user
    @Test
    @Order(7)
    public void deleteRegisteredUser() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/delete-account")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("message").asText().contains("Successfully deleted your account"));
    }

}
