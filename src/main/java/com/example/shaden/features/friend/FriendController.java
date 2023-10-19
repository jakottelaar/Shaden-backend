package com.example.shaden.features.friend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.features.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/add/{friendId}")
    public ResponseEntity<ResponseData> addFriend(@PathVariable Long friendId) {
        friendService.addFriend(friendId);
        
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(HttpStatus.CREATED.value());
        responseData.setMessage("Friend added successfully");

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

}
