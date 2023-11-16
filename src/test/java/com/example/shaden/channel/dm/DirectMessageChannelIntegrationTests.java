package com.example.shaden.channel.dm;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.shaden.config.JsonParserUtil;
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
    @Qualifier("getTestUserToken1")
    private String testUserToken1;

    @Autowired
    @Qualifier("getTestUserToken1")
    private String testUserToken2;

    @Autowired
    @Qualifier("getTestUser1")
    private User testFriend1;

    @Autowired
    @Qualifier("getTestUser2")
    private User testFriend2;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DMChannelRepository dmChannelRepository;

    private Logger LOGGER = LoggerFactory.getLogger(DirectMessageChannelIntegrationTests.class);

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
