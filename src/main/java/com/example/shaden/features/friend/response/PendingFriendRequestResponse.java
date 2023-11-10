package com.example.shaden.features.friend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingFriendRequestResponse {
    
    private Long requestId;    
    private Long friendId;
    private String friendUsername;
    private String status;
    private String requestType;
    
}
