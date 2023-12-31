package com.example.shaden.features.statistics.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatisticsResponse {
    
    @JsonProperty("total_users")
    private Long totalUsers;
    @JsonProperty("total_friendships")
    private Long totalFriendships;
    @JsonProperty("total_messages")
    private Long totalMessages;

}
