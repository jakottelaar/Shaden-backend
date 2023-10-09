package com.example.shaden.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("prod-test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    private static String accessToken;
   	private static final Logger LOG = LoggerFactory.getLogger(AuthIntegrationTest.class);

    @Test
    @Order(1)
    public void accountRegistrationTest() throws Exception {
        String jsonRequest = "{\"username\":\"test\",\"email\":\"test@mail.com\",\"password\":\"Testpass1234\"}";
   
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
   
    @Test
    @Order(2)
    public void testLogin() throws Exception {
        String jsonRequest = "{\"email\":\"test@mail.com\",\"password\":\"Testpass1234\"}";
   
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
   
        String responseBody = result.getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(responseBody);
        accessToken = jsonResponse.getString("access_token");

    }
   
    @Test
    @Order(3)
    public void deleteRegisteredUser() throws Exception {
   
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/delete-account")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
