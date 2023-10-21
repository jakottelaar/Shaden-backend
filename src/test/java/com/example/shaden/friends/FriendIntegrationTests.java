package com.example.shaden.friends;

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
public class FriendIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(FriendIntegrationTests.class);

    private static String testFriendUserToken1;
    private static String testFriendUserToken2;
    private static String testFriendUserToken3;
    private static Long testFriendId1 = 1L;
    private static Long testFriendId2 = 2L;
    private static Long testFriendId3 = 3L;

    @BeforeAll
    public void setup() {

        RegisterRequest testFriendUser1 = new RegisterRequest("testFriendUser1","testFriend1@mail.com", "testFriend1");
        RegisterRequest testFriendUser2 = new RegisterRequest("testFriendUser2", "testFriend2@mail.com", "testFriend2");
        RegisterRequest testFriendUser3 = new RegisterRequest("testFriendUser3", "testFriend3@mail.com", "testFriend3");

        authenticationService.register(testFriendUser1);
        authenticationService.register(testFriendUser2);
        authenticationService.register(testFriendUser3);

        try {
            testFriendUserToken1 = obtainAccessToken("testFriend1@mail.com", "testFriend1");
            testFriendUserToken2 = obtainAccessToken("testFriend2@mail.com", "testFriend2");
            testFriendUserToken3 = obtainAccessToken("testFriend3@mail.com", "testFriend3");
            testFriendId1 = userRepository.findByEmail("testFriend1@mail.com").get().getId();
            testFriendId2 = userRepository.findByEmail("testFriend2@mail.com").get().getId();
            testFriendId3 = userRepository.findByEmail("testFriend3@mail.com").get().getId();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AfterAll
    public void cleanup() {
        userRepository.deleteById(testFriendId1);
        userRepository.deleteById(testFriendId2);
        userRepository.deleteById(testFriendId3);
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        String token = authenticationService.authenticate(authenticationRequest).getAccessToken();
        return token;

    }


    //User 1 sends friend request to user 2
    @Test
    @Order(1)
    public void testSentFriendRequest() throws Exception {

        String uri = "/api/friends/add/" + testFriendId2;

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 201);
        assert(jsonResponse.get("message").asText().contains("Friend request sent successfully"));
    }

    //User 2 accepts friend request from user 1
    @Test
    @Order(2)
    public void testFriend2AcceptsFriend1FriendRequest() throws Exception {

        String uri = "/api/friends/" + testFriendId1 + "/accept";

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .header("Authorization", "Bearer " + testFriendUserToken2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().contains("Friend request accepted successfully"));

    }

    //User 1 removes friend 2
    @Test
    @Order(3)
    public void testFriend1RemovesFriend2() throws Exception {

        String uri = "/api/friends/" + testFriendId2;

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 204);
        assert(jsonResponse.get("message").asText().contains("Friend removed successfully"));

    }


}
