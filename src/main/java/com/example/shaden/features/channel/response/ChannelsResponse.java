package com.example.shaden.features.channel.response;

import java.util.List;

import com.example.shaden.features.channel.dm.response.DMChannelResponse;
import com.example.shaden.features.channel.group.response.GroupChannelResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChannelsResponse {
    
    @JsonProperty("dm_channels")
    private List<DMChannelResponse> dmChannels;
    @JsonProperty("group_channels")
    private List<GroupChannelResponse> groupChannels;

}
