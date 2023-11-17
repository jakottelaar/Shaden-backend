package com.example.shaden.channel.dm;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.shaden.config.JsonParserUtil;
import com.example.shaden.features.authentication.AuthenticationService;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.channel.dm.DMChannelRepository;
import com.example.shaden.features.channel.dm.request.CreateDmChannelRequest;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class DirectMessageChannelIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DMChannelRepository dmChannelRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private Logger LOGGER = LoggerFactory.getLogger(DirectMessageChannelIntegrationTests.class);

    private static String testUserToken1;
    private static String testUserToken2;
    private static User testFriend1;
    private static User testFriend2;

    @BeforeAll
    public void setup() throws Exception {
        // Create test users
        RegisterRequest testUser1 = new RegisterRequest("testUser1", "testuser1@mail.com", "testMan1");
        RegisterRequest testUser2 = new RegisterRequest("testUser2", "testuser2@mail.com", "testMan2");
        authenticationService.register(testUser1);
        authenticationService.register(testUser2);

        // Authenticate test users
        AuthenticationRequest authenticationRequestUser1 = new AuthenticationRequest("testuser1@mail.com", "testMan1");
        AuthenticationRequest authenticationRequestUser2 = new AuthenticationRequest("testuser2@mail.com", "testMan2");

        testUserToken1 = authenticationService.authenticate(authenticationRequestUser1).getAccessToken();
        testUserToken2 = authenticationService.authenticate(authenticationRequestUser2).getAccessToken();

        // Retrieve test users from the repository
        testFriend1 = userRepository.findByEmail("testuser1@mail.com").orElse(null);
        testFriend2 = userRepository.findByEmail("testuser2@mail.com").orElse(null);
    }

    @AfterAll
    public void tearDown() {
        dmChannelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void Create_DM_Channel() throws Exception {
        
        String uri = "/api/dm-channels";

        CreateDmChannelRequest request = new CreateDmChannelRequest();
        request.setUser1Id(testFriend1.getId());
        request.setUser2Id(testFriend2.getId());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .header("Authorization", "Bearer " + testUserToken1)
                .content(JsonParserUtil.asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        LOGGER.info(jsonResponse.toString());        

        assert(jsonResponse.get("status").asInt() == 201);
        assert(jsonResponse.get("message").asText().equals("Successfully created a DM channel"));
        assert(jsonResponse.get("results").get("user1_id").asLong() == testFriend1.getId());
        assert(jsonResponse.get("results").get("user2_id").asLong() == testFriend2.getId());

    }

}
