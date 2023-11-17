package com.example.shaden.features.channel.dm.response;

import java.util.List;

import com.example.shaden.features.messaging.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DMChannelResponse {
    
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("user1_id")
    private Long user1Id;
    @JsonProperty("user2_id")
    private Long user2Id;
    @JsonProperty("messages")
    private List<MessageResponse> messages;

}
