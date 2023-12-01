package com.example.shaden.features.channel;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.shaden.features.channel.dm.DMChannel;
import com.example.shaden.features.channel.dm.DMChannelRepository;
import com.example.shaden.features.channel.dm.response.DMChannelResponse;
import com.example.shaden.features.channel.group.GroupChannel;
import com.example.shaden.features.channel.group.GroupChannelRepository;
import com.example.shaden.features.channel.group.response.GroupChannelResponse;
import com.example.shaden.features.channel.response.ChannelsResponse;
import com.example.shaden.features.user.UserPrincipal;
import com.example.shaden.features.user.response.UserProfileResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final DMChannelRepository dmChannelRepository;
    private final GroupChannelRepository groupChannelRepository;

    public ChannelsResponse getAllChannels() {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        long userId = userPrincipal.getUserId();
        
        List<DMChannel> dmChannels = dmChannelRepository.findAllByUserId(userId);
        
        List<DMChannelResponse> convertedDmChannels = dmChannels.stream().map(dmChannel -> 
            DMChannelResponse.builder()
            .channelId(dmChannel.getId())
            .user1Id(dmChannel.getUser1().getId())
            .user2Id(dmChannel.getUser2().getId())
            .channelType(dmChannel.getChannelType().toString())
            .build()
        ).toList();

        List<GroupChannel> groupChannels = groupChannelRepository.findAllByUserId(userId);

        List<GroupChannelResponse> convertedGroupChannels = groupChannels.stream().map(groupChannel -> 
            GroupChannelResponse.builder()
            .channelId(groupChannel.getId())
            .creatorId(groupChannel.getCreator().getId())
            .users(groupChannel.getUsers().stream().map(user -> 
                UserProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build())
                .toList())
            .channelType(groupChannel.getChannelType().toString())
            .build()
        ).toList();

        ChannelsResponse response = ChannelsResponse.builder()
        .dmChannels(convertedDmChannels)
        .groupChannels(convertedGroupChannels)
        .build();

        return response;

    }
    
}
