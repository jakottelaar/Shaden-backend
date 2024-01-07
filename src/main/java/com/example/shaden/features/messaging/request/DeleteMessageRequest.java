package com.example.shaden.features.messaging.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteMessageRequest {

    @JsonProperty("message_id")
    private Long messageId;
    @JsonProperty("channel_id")
    private Long channelId;

}