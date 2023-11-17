package com.example.shaden.features.channel.dm;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.shaden.features.channel.dm.request.CreateDmChannelRequest;
import com.example.shaden.features.channel.dm.response.CreatedDmChannelResponse;
import com.example.shaden.features.channel.dm.response.DMChannelResponse;
import com.example.shaden.features.messaging.response.MessageResponse;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DMChannelService {
    
    private final DMChannelRepository dmChannelRepository;

    private final UserRepository userRepository;

    public CreatedDmChannelResponse createDMChannel(CreateDmChannelRequest request) {
        
        User user1 = userRepository.findById(request.getUser1Id()).orElseThrow();

        User user2 = userRepository.findById(request.getUser2Id()).orElseThrow();

        if (user1.getId() == user2.getId()) {
            throw new RuntimeException("You can't create a DM channel with yourself");
        }

        DMChannel existingDMChannel = dmChannelRepository.findDMChannelByUser1IdAndUser2Id(user1.getId(), user2.getId());

        if (existingDMChannel != null) {
            throw new RuntimeException("You already have a DM channel with this user");
        }

        DMChannel dmChannel = DMChannel.builder()
        .user1(user1)
        .user2(user2)
        .build();

        dmChannel.setCreatedDate(LocalDateTime.now());

        dmChannelRepository.save(dmChannel);

        return CreatedDmChannelResponse.builder()
        .channelId(dmChannel.getId())
        .user1Id(user1.getId())
        .user2Id(user2.getId())
        .build();

    }

    public DMChannelResponse getDMChannelWithId(Long channelId) {

        Optional<DMChannel> dmChannel = dmChannelRepository.findById(channelId);

        if (dmChannel.isEmpty()) {
            throw new RuntimeException("DM channel not found");
        }

        DMChannelResponse response = DMChannelResponse.builder()
        .channelId(dmChannel.get().getId())
        .user1Id(dmChannel.get().getUser1().getId())
        .user2Id(dmChannel.get().getUser2().getId())
        .messages(dmChannel.get().getMessages().stream().map(message -> 
            MessageResponse.builder()
            .messageId(message.getId())
            .senderId(message.getSender().getId())
            .content(message.getContent())
            .build()
        ).toList())
        .build();

        return response;
    }
}
