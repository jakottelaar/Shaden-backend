package com.example.shaden.authentication;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.shaden.config.JsonParserUtil;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static String accessToken;
    private static String refreshToken;

    @AfterAll
    public void cleanUp() throws Exception {
        userRepository.deleteAll();
    }

    //Test account registration
    @Test
    @Order(1)
    public void accountRegistrationTest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("authTestUsername");
        registerRequest.setEmail("authtest@mail.com");
        registerRequest.setPassword("Testpass1234");
   
        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(JsonParserUtil.asJsonString(registerRequest))
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
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("");
        registerRequest.setEmail("testmail.com");
        registerRequest.setPassword("Testpass");
    

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(JsonParserUtil.asJsonString(registerRequest))
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
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("authTestUsername");
        registerRequest.setEmail("authtest@mail.com");
        registerRequest.setPassword("Testpass1234");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(JsonParserUtil.asJsonString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);
        assert(jsonResponse.get("message").asText().contains("Email already exists"));
    }
    
    //Test login
    @Test
    @Order(4)
    public void testLogin() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("authtest@mail.com");
        authenticationRequest.setPassword("Testpass1234");
   
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParserUtil.asJsonString(authenticationRequest)))
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

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("authtest@mail.com");
        authenticationRequest.setPassword("Testpass1");
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParserUtil.asJsonString(authenticationRequest)))
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

}
