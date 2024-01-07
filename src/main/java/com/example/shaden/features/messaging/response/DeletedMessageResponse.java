package com.example.shaden.features.messaging.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeletedMessageResponse {
    
    @JsonProperty("message_id")
    private Long messageId;
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("deleted")
    private boolean deleted;

}
