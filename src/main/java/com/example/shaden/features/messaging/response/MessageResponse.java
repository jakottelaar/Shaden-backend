package com.example.shaden.features.messaging.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MessageResponse {
    
    @JsonProperty("message_id")
    private Long messageId;
    @JsonProperty("content")
    private String content;
    @JsonProperty("sender_id")
    private Long senderId;
    @JsonProperty("sender_username")
    private String senderUsername;
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("created_date")
    private String createdDate;
    @JsonProperty("last_modified_date")
    private String lastModifiedDate;

}
