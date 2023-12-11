package com.example.shaden.features.channel.dm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.features.channel.ChannelType;
import com.example.shaden.features.channel.dm.request.CreateDmChannelRequest;
import com.example.shaden.features.channel.dm.response.DMChannelResponse;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserPrincipal;
import com.example.shaden.features.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DMChannelService {
    
    private final DMChannelRepository dmChannelRepository;

    private final UserRepository userRepository;

    public DMChannelResponse createDMChannel(CreateDmChannelRequest request) {

        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        long creatorId = userPrincipal.getUserId();

        User creator = userRepository.findById(creatorId).orElseThrow();

        User participant = userRepository.findById(request.getUserId()).orElseThrow();

        if (creator.getId() == participant.getId()) {
            throw new IllegalArgumentException("You can't create a DM channel with yourself");
        }

        DMChannel existingDMChannel = dmChannelRepository.findDMChannelByUserIds(creator.getId(), participant.getId());

        if (existingDMChannel != null) {
            throw new IllegalArgumentException("You already have a DM channel with this user");
        }

        DMChannel dmChannel = new DMChannel();
        dmChannel.setCreator(creator);
        dmChannel.setParticipant(participant);
        dmChannel.setChannelType(ChannelType.DIRECT);
        dmChannel.setCreatedDate(LocalDateTime.now());

        dmChannelRepository.save(dmChannel);

        return DMChannelResponse.builder()
        .channelId(dmChannel.getId())
        .creatorId(dmChannel.getCreator().getId())
        .participantId(dmChannel.getParticipant().getId())
        .channelType(dmChannel.getChannelType().toString())
        .build();

    }

    public DMChannelResponse getDMChannelWithId(Long channelId) {

        Optional<DMChannel> dmChannel = dmChannelRepository.findById(channelId);

        if (dmChannel.isEmpty()) {
            throw new ResourceNotFoundException("DM channel not found");
        }

        DMChannelResponse response = DMChannelResponse.builder()
        .channelId(dmChannel.get().getId())
        .creatorId(dmChannel.get().getCreator().getId())
        .participantId(dmChannel.get().getParticipant().getId())
        .channelType(dmChannel.get().getChannelType().toString())
        .build();

        return response;
    }

    public DMChannelResponse getDMChannelsWithUserId(Long userId2) {
            
        Optional<User> user = userRepository.findById(userId2);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        long userId1 = userPrincipal.getUserId();

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        DMChannel dmChannel = dmChannelRepository.findDMChannelByUserIds(userId1, userId2);

        if (dmChannel == null) {
            throw new ResourceNotFoundException("DM channel not found");
        }

        DMChannelResponse response = DMChannelResponse.builder()
        .channelId(dmChannel.getId())
        .creatorId(dmChannel.getCreator().getId())
        .participantId(dmChannel.getParticipant().getId())
        .channelType(dmChannel.getChannelType().toString())
        .build();

        return response;
    }

    public void deleteDMChannelWithId(Long channelId) {
        
        Optional<DMChannel> dmChannel = dmChannelRepository.findById(channelId);

        if (dmChannel.isEmpty()) {
            throw new ResourceNotFoundException("DM channel not found");
        }

        dmChannelRepository.deleteById(channelId);

    }

    public List<DMChannelResponse> getAllDMChannels() {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        long userId1 = userPrincipal.getUserId();

        List<DMChannel> dmChannels = dmChannelRepository.findAllByUserId(userId1);

        List<DMChannelResponse> convertedDmChannels = dmChannels.stream().map(dmChannel -> 
            DMChannelResponse.builder()
            .channelId(dmChannel.getId())
            .creatorId(dmChannel.getCreator().getId())
            .participantId(dmChannel.getParticipant().getId())
            .channelType(dmChannel.getChannelType().toString())
            .build()
        ).toList();

        return convertedDmChannels;

    }
}
