package com.example.shaden.features.friend.response;

import com.example.shaden.features.friend.FriendshipStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponse {
    
    private Long friendId;
    private String friendUsername;
    private FriendshipStatus status;

}
