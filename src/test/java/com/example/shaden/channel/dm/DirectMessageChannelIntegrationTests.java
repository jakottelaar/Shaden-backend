package com.example.shaden.channel.dm;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.shaden.features.authentication.AuthenticationService;
import com.example.shaden.features.authentication.request.AuthenticationRequest;
import com.example.shaden.features.authentication.request.RegisterRequest;
import com.example.shaden.features.channel.dm.DMChannelRepository;
import com.example.shaden.features.channel.dm.request.CreateDmChannelRequest;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    private Gson gson = new Gson();

    private Logger LOGGER = LoggerFactory.getLogger(DirectMessageChannelIntegrationTests.class);

    private static String testUserToken1;
    private static String testUserToken2;
    private static User testFriend1;
    private static User testFriend2;
    private static Long testDmChannelId1;

    @BeforeAll
    public void setup() throws Exception {
        // Create test users
        RegisterRequest testUser1 = new RegisterRequest("testUser1", "testuser1@mail.com", "testMan1");
        RegisterRequest testUser2 = new RegisterRequest("testUser2", "testuser2@mail.com", "testMan2");

        MockHttpServletResponse response = new MockHttpServletResponse();
    
        authenticationService.register(testUser1, response);
        authenticationService.register(testUser2, response);

        // Authenticate test users
        AuthenticationRequest authenticationRequestUser1 = new AuthenticationRequest("testuser1@mail.com", "testMan1");
        AuthenticationRequest authenticationRequestUser2 = new AuthenticationRequest("testuser2@mail.com", "testMan2");

        testUserToken1 = authenticationService.authenticate(authenticationRequestUser1, response).getAccessToken();
        testUserToken2 = authenticationService.authenticate(authenticationRequestUser2, response).getAccessToken();

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
    public void Create_dm_channel() throws Exception {
        String uri = "/api/channels/direct";

        CreateDmChannelRequest request = new CreateDmChannelRequest();
        request.setUserId(testFriend2.getId());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .header("Authorization", "Bearer " + testUserToken1)
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Successfully created a DM channel"))
                .andExpect(jsonPath("$.results.user1_id").value(testFriend1.getId()))
                .andExpect(jsonPath("$.results.user2_id").value(testFriend2.getId()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonObject parsedResponse = JsonParser.parseString(jsonResponse).getAsJsonObject();

        testDmChannelId1 = parsedResponse.get("results").getAsJsonObject().get("channel_id").getAsLong();
    }

    @Test
    @Order(2)
    public void Get_dm_channel_with_user_id() throws Exception {
        String uri = "/api/channels/direct/user/" + testFriend2.getId();

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header("Authorization", "Bearer " + testUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved DM channel"))
                .andExpect(jsonPath("$.results.user1_id").value(testFriend1.getId()))
                .andExpect(jsonPath("$.results.user2_id").value(testFriend2.getId()));
    }

    @Test
    @Order(3)
    public void Get_dm_channel_with_id() throws Exception {
        String uri = "/api/channels/direct/" + testDmChannelId1;

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header("Authorization", "Bearer " + testUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully retrieved DM channel"))
                .andExpect(jsonPath("$.results.user1_id").value(testFriend1.getId()))
                .andExpect(jsonPath("$.results.user2_id").value(testFriend2.getId()));
    }

    @Test
    @Order(4)
    public void Delete_dm_channel_with_id() throws Exception {
        String uri = "/api/channels/direct/" + testDmChannelId1;

        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .header("Authorization", "Bearer " + testUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Successfully deleted a DM channel"));
    }

}
