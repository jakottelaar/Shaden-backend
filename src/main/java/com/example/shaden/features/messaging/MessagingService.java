package com.example.shaden.features.messaging;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.features.channel.Channel;
import com.example.shaden.features.channel.ChannelRepository;
import com.example.shaden.features.messaging.request.MessageRequest;
import com.example.shaden.features.messaging.response.MessageResponse;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessagingService {
    
    private final MessageRepository messageRepository;
    
    private final ChannelRepository channelRepository;

    private final UserRepository userRepository;

    	private static final Logger LOG = LoggerFactory.getLogger(MessagingService.class);

    public MessageResponse saveMessage(MessageRequest messageRequest) {
        LOG.info("Message request: {}", messageRequest);
        Channel channel = channelRepository.findById(messageRequest.getChannelId()).orElseThrow(() -> new ResourceNotFoundException("Channel not found"));

        User sender = userRepository.findById(messageRequest.getSenderId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Message message = Message.builder()
                .channel(channel)
                .sender(sender)
                .content(messageRequest.getContent())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        MessageResponse messageResponse = MessageResponse.builder()
                .messageId(message.getId())
                .channelId(message.getChannel().getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .createdDate(message.getCreatedDate().toString())
                .lastModifiedDate(message.getLastModifiedDate().toString())
                .build();

            
        return messageResponse;
    }
    

}
