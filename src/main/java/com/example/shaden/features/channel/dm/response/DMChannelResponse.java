package com.example.shaden.features.channel.dm.response;

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
    @JsonProperty("creator_id")
    private Long creatorId;
    @JsonProperty("participant_id")
    private Long participantId;
    @JsonProperty("channel_type")
    private String channelType;

}
