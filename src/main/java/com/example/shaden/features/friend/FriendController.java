package com.example.shaden.features.friend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.features.ResponseData;
import com.example.shaden.features.friend.request.FriendRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/requests")
    public ResponseEntity<ResponseData> sentFriendRequest(@RequestBody FriendRequest friendRequest) {
        String username = friendRequest.getUsername();
        friendService.sentFriendRequest(username);

        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.CREATED.value());
        responseData.setMessage("Friend request sent successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseData> getAllFriends() {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("Friends retrieved successfully");
        responseData.setResults(friendService.getAllFriends());

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<ResponseData> getFriendById(@PathVariable Long friendId) {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("Friend retrieved successfully");
        responseData.setResults(friendService.getFriendById(friendId));

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @GetMapping("/pending")
    public ResponseEntity<ResponseData> getPendingFriendRequests() {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("Pending friend requests retrieved successfully");
        responseData.setResults(friendService.getPendingFriendRequests());

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @PatchMapping("/requests/{friendId}/accept")
    public ResponseEntity<ResponseData> acceptFriend(@PathVariable Long friendId) {
        friendService.acceptFriend(friendId);

        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("Friend request accepted successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @PatchMapping("/requests/{friendId}/reject")
    public ResponseEntity<ResponseData> rejectFriend(@PathVariable Long friendId) {
        friendService.rejectFriend(friendId);

        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("Friend request rejected successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @DeleteMapping("/{friendId}/cancel")
    public ResponseEntity<ResponseData> cancelOutgoingFriendRequest(@PathVariable Long friendId) {
        friendService.cancelOutgoingFriendRequest(friendId);

        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("Outgoing friend request cancelled successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseData);

    } 

    @DeleteMapping("/{friendId}")
    public ResponseEntity<ResponseData> removeFriend(@PathVariable Long friendId) {
        friendService.removeFriend(friendId);

        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.OK.value());
        responseData.setMessage("Friend removed successfully");

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

}
