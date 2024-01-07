package com.example.shaden.features.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.shaden.features.messaging.request.DeleteMessageRequest;
import com.example.shaden.features.messaging.request.MessageRequest;
import com.example.shaden.features.messaging.response.DeletedMessageResponse;
import com.example.shaden.features.messaging.response.MessageResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessagingController {
    
    private final MessagingService messagingService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(MessagingController.class);

    @MessageMapping("/send-message")
    public void sendMessage(@Payload MessageRequest messageRequest) {
        MessageResponse messageResponse = messagingService.saveMessage(messageRequest);

        messagingTemplate.convertAndSend("/topic/" + messageRequest.getChannelId(), messageResponse);
    }

    @MessageMapping("/delete-message")
    public void deleteMessage(@Payload DeleteMessageRequest deleteMessageRequest) {
        LOG.info("Delete message request: {}", deleteMessageRequest);

        messagingService.deleteMessage(deleteMessageRequest.getMessageId(), deleteMessageRequest.getChannelId());

        DeletedMessageResponse deletedMessageResponse = DeletedMessageResponse.builder()
                .messageId(deleteMessageRequest.getMessageId())
                .channelId(deleteMessageRequest.getChannelId())
                .deleted(true)
                .build();

        messagingTemplate.convertAndSend("/topic/" + deleteMessageRequest.getChannelId(), deletedMessageResponse);
    }

}
