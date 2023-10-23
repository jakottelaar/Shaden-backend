package com.example.shaden.friends;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.shaden.features.ResponseData;
import com.example.shaden.features.friend.FriendController;
import com.example.shaden.features.friend.FriendService;
import com.example.shaden.features.friend.FriendshipStatus;
import com.example.shaden.features.friend.response.FriendResponse;

@ExtendWith(MockitoExtension.class)
public class FriendControllerUnitTests {
    
    @InjectMocks
    private FriendController friendController;

    @Mock
    private FriendService friendService;

    @Test
    public void testAddFriendEndpoint() {
        Mockito.doNothing().when(friendService).sentFriendRequest(any(String.class));

        ResponseEntity<ResponseData> responseEntity = friendController.sentFriendRequest("TestFriend");
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ResponseData responseData = responseEntity.getBody();
        assertNotNull(responseData);
        assertEquals(201, responseData.getStatusCode());
        assertEquals("Friend request sent successfully", responseData.getMessage());
    }

    @Test
    public void testGetAllFriendsEndpoint() {
        List<FriendResponse> friendResponses = new ArrayList<>();

        Mockito.when(friendService.getAllFriends()).thenReturn(friendResponses);

        ResponseEntity<ResponseData> responseEntity = friendController.getAllFriends();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ResponseData responseData = responseEntity.getBody();
        assertNotNull(responseData);
        assertEquals(200, responseData.getStatusCode());
        assertEquals("Friends retrieved successfully", responseData.getMessage());
        assertEquals(friendResponses, responseData.getResults());
    }

    @Test
    public void testGetFriendByIdEndpoint() {
        Long friendId = 1L;
        FriendResponse friendResponse = new FriendResponse(friendId, "TestFriend", FriendshipStatus.ACCEPTED);

        Mockito.when(friendService.getFriendById(friendId)).thenReturn(friendResponse);

        ResponseEntity<ResponseData> responseEntity = friendController.getFriendById(friendId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ResponseData responseData = responseEntity.getBody();
        assertNotNull(responseData);
        assertEquals(200, responseData.getStatusCode());
        assertEquals("Friend retrieved successfully", responseData.getMessage());
        assertEquals(friendResponse, responseData.getResults());
    }
}
