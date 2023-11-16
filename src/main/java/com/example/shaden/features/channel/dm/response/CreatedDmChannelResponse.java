package com.example.shaden.features.channel.dm.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreatedDmChannelResponse {
    
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("user1_id")
    private Long user1Id;
    @JsonProperty("user2_id")
    private Long user2Id;

}
