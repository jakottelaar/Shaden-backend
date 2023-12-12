package com.example.shaden.features.messaging;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.shaden.features.messaging.request.MessageRequest;
import com.example.shaden.features.messaging.response.MessageResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessagingController {
    
    private final MessagingService messagingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send-message")
    public void sendMessage(@Payload MessageRequest messageRequest) {
        MessageResponse messageResponse = messagingService.saveMessage(messageRequest);

        messagingTemplate.convertAndSend("/queue/" + messageRequest.getChannelId(), messageResponse);
    }

}
