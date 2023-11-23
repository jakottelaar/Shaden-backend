package com.example.shaden.features.channel.dm;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.shaden.exception.custom.ResourceNotFoundException;
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

        long userId1 = userPrincipal.getUserId();

        User user1 = userRepository.findById(userId1).orElseThrow();

        User user2 = userRepository.findById(request.getUserId()).orElseThrow();

        if (user1.getId() == user2.getId()) {
            throw new IllegalArgumentException("You can't create a DM channel with yourself");
        }

        DMChannel existingDMChannel = dmChannelRepository.findDMChannelByUserIds(user1.getId(), user2.getId());

        if (existingDMChannel != null) {
            throw new IllegalArgumentException("You already have a DM channel with this user");
        }

        DMChannel dmChannel = DMChannel.builder()
        .user1(user1)
        .user2(user2)
        .build();

        dmChannel.setCreatedDate(LocalDateTime.now());

        dmChannelRepository.save(dmChannel);

        return DMChannelResponse.builder()
        .channelId(dmChannel.getId())
        .user1Id(user1.getId())
        .user2Id(user2.getId())
        .build();

    }

    public DMChannelResponse getDMChannelWithId(Long channelId) {

        Optional<DMChannel> dmChannel = dmChannelRepository.findById(channelId);

        if (dmChannel.isEmpty()) {
            throw new ResourceNotFoundException("DM channel not found");
        }

        DMChannelResponse response = DMChannelResponse.builder()
        .channelId(dmChannel.get().getId())
        .user1Id(dmChannel.get().getUser1().getId())
        .user2Id(dmChannel.get().getUser2().getId())
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
        .user1Id(dmChannel.getUser1().getId())
        .user2Id(dmChannel.getUser2().getId())
        .build();

        return response;
    }
}
