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
import com.example.shaden.features.friend.FriendshipStatus;
import com.example.shaden.features.user.User;
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
    private static User testFriendUser1;
    private static User testFriendUser2;
    private static User testFriendUser3;

    @BeforeAll
    public void setup() {

        RegisterRequest testUser1Request = new RegisterRequest("testFriendUser1","testFriend1@mail.com", "testFriend1");
        RegisterRequest testUser2Request = new RegisterRequest("testFriendUser2", "testFriend2@mail.com", "testFriend2");
        RegisterRequest testUser3Request = new RegisterRequest("testFriendUser3", "testFriend3@mail.com", "testFriend3");

        authenticationService.register(testUser1Request);
        authenticationService.register(testUser2Request);
        authenticationService.register(testUser3Request);

        try {
            testFriendUserToken1 = obtainAccessToken("testFriend1@mail.com", "testFriend1");
            testFriendUserToken2 = obtainAccessToken("testFriend2@mail.com", "testFriend2");
            testFriendUserToken3 = obtainAccessToken("testFriend3@mail.com", "testFriend3");
            testFriendUser1 = userRepository.findByEmail("testFriend1@mail.com").get();
            testFriendUser2 = userRepository.findByEmail("testFriend2@mail.com").get();
            testFriendUser3 = userRepository.findByEmail("testFriend3@mail.com").get();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AfterAll
    public void cleanup() {
        userRepository.deleteAll();
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

        String uri = "/api/friends/add/" + testFriendUser2.getUsername();

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

        String uri = "/api/friends/" + testFriendUser1.getId() + "/accept";

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .header("Authorization", "Bearer " + testFriendUserToken2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().contains("Friend request accepted successfully"));

    }

    //User 1 fetches friends list and assert it only contains accepted friends
    @Test
    @Order(3)
    public void testFriend1FetchesFriendsList() throws Exception {
        String uri = "/api/friends/list";

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        LOGGER.info("jsonResponse: " + jsonResponse.toString());
        
        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().contains("Friends retrieved successfully"));
        assert(jsonResponse.get("results").get(0).get("status").asText().equals(FriendshipStatus.ACCEPTED.toString()));

    }

    //User 1 fetches friend 2 by id and asserts the friendship is accepted
    @Test
    @Order(4)
    public void testFriend1FetchesFriendById() throws Exception {
        String uri = "/api/friends/" + testFriendUser2.getId();

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().contains("Friend retrieved successfully"));
        assert(jsonResponse.get("results").get("status").asText().equals(FriendshipStatus.ACCEPTED.toString()));

    }

    //User 1 removes friend 2
    @Test
    @Order(5)
    public void testFriend1RemovesFriend2() throws Exception {

        String uri = "/api/friends/" + testFriendUser2.getId();

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 204);
        assert(jsonResponse.get("message").asText().contains("Friend removed successfully"));

    }

    //User 1 sends friend request to user 3
    @Test
    @Order(6)
    public void testFriend1SendsFriendRequestToFriend3() throws Exception {

        String uri = "/api/friends/add/" + testFriendUser3.getUsername();

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 201);
        assert(jsonResponse.get("message").asText().contains("Friend request sent successfully"));

    }

    //User 1 fetches all pending friend requests and asserts it contains the friend request sent to user 3
    @Test
    @Order(7)
    public void testFriend1GetAllPendingFriendRequests() throws Exception {
        String uri = "/api/friends/pending";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().contains("Pending friend requests retrieved successfully"));
        assert(jsonResponse.get("results").get(0).get("status").asText().equals(FriendshipStatus.PENDING.toString()));
        assert(jsonResponse.get("results").get(0).get("requestType").asText().equals("OUTGOING"));
        assert(jsonResponse.get("results").get(0).get("friendUsername").asText().equals("testFriendUser3"));
    }

    //User 3 fetches all pending friend requests and asserts it contains the friend request sent to user 3
    @Test
    @Order(8)
    public void testFriend3GetAllPendingFriendRequests() throws Exception {
        String uri = "/api/friends/pending";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header("Authorization", "Bearer " + testFriendUserToken3)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().contains("Pending friend requests retrieved successfully"));
        assert(jsonResponse.get("results").get(0).get("status").asText().equals(FriendshipStatus.PENDING.toString()));
        assert(jsonResponse.get("results").get(0).get("requestType").asText().equals("INCOMING"));
        assert(jsonResponse.get("results").get(0).get("friendUsername").asText().equals("testFriendUser1"));
    }

    //User 3 declines friend request from user 1
    @Test
    @Order(9)
    public void testFriend3DeclinesFriendRequestFromFriend1() throws Exception {

        String uri = "/api/friends/" + testFriendUser1.getId() + "/reject";

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .header("Authorization", "Bearer " + testFriendUserToken3)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().contains("Friend request rejected successfully"));

    }
    
    //User 1 resent friend request to user 3
    @Test
    @Order(10)
    public void testResentFriendRequestFromFriend1ToFriend3() throws Exception {

        String uri = "/api/friends/add/" + testFriendUser3.getUsername();

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 201);
        assert(jsonResponse.get("message").asText().contains("Friend request sent successfully"));

    }

    //User 3 accepts friend request from user 1
    @Test
    @Order(11)
    public void testFriend3AcceptsFriend1FriendRequest() throws Exception {

        String uri = "/api/friends/" + testFriendUser1.getId() + "/accept";

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .header("Authorization", "Bearer " + testFriendUserToken3)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 200);
        assert(jsonResponse.get("message").asText().contains("Friend request accepted successfully"));

    }

    //User 1 removes friend 3
    @Test
    @Order(12)
    public void testRemoveFriend1RemovesFriend3() throws Exception {

        String uri = "/api/friends/" + testFriendUser3.getId();

        MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .header("Authorization", "Bearer " + testFriendUserToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andReturn();

        JsonNode jsonResponse = JsonParserUtil.parseJsonResponse(result);

        assert(jsonResponse.get("status").asInt() == 204);
        assert(jsonResponse.get("message").asText().contains("Friend removed successfully"));

    }
    

}
