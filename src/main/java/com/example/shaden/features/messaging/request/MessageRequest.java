package com.example.shaden.features.messaging.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MessageRequest {
    
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("sender_id")
    private Long senderId;
    private String content;

}
