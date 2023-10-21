package com.example.shaden.features.friend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingFriendRequestResponse {
    
    private Long requestId;    
    private Long friendId; // Combined sender and receiver into a single 'friendId'
    private String friendUsername;
    private String status;
    private String requestType;
    
}
