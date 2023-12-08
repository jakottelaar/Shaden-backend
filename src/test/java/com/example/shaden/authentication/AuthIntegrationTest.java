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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.user.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class AuthIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static String accessToken;
    private static String refreshToken;

    private Gson gson = new Gson();

    @AfterAll
    public void cleanUp() throws Exception {
        userRepository.deleteAll();
    }

    //Test account registration
    @Test
    @Order(1)
    public void Successful_account_registration() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("authTestUsername");
        registerRequest.setEmail("authtest@mail.com");
        registerRequest.setPassword("Testpass1234");
    
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(gson.toJson(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.results.access_token").isString())
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andReturn();

        refreshToken = result.getResponse().getCookie("refresh_token").getValue();
                
    }
    

    // Test invalid input for account registration
    @Test
    @Order(2)
    public void Invalid_input_account_registration() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("");
        registerRequest.setEmail("testmail.com");
        registerRequest.setPassword("Testpass");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
            .content(gson.toJson(registerRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", Matchers.containsInAnyOrder(
                    "Invalid email format",
                    "Username is required",
                    "Password must contain at least one letter, one digit, and be 8 or more characters long"
            )));

    }

    // Test account registration with email that already exists
    @Test
    @Order(3)
    public void Account_registration_with_email_that_already_exists() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("authTestUsername");
        registerRequest.setEmail("authtest@mail.com");
        registerRequest.setPassword("Testpass1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(gson.toJson(registerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    // Test login
    @Test
    @Order(4)
    public void Successful_login() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("authtest@mail.com");
        authenticationRequest.setPassword("Testpass1234");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User authenticated successfully"))
                .andExpect(jsonPath("$.results.access_token").isString())
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonObject parsedResponse = JsonParser.parseString(jsonResponse).getAsJsonObject();

        accessToken = parsedResponse.get("results").getAsJsonObject().get("access_token").getAsString();
    }

    // Test invalid credentials login
    @Test
    @Order(5)
    public void Invalid_credentials_login() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("authtest@mail.com");
        authenticationRequest.setPassword("Testpass1");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // Test refresh token
    @Test
    @Order(6)
    public void Get_refresh_token() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.results.access_token").isString())
                .andExpect(cookie().httpOnly("refresh_token", true));
    }

}
