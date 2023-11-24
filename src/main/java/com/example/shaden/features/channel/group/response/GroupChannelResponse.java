package com.example.shaden.features.channel.group.response;

import java.util.List;

import com.example.shaden.features.user.response.UserProfileResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GroupChannelResponse {
    
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("creator_id")
    private Long creatorId;
    @JsonProperty("users")
    private List<UserProfileResponse> users;

}
