package com.example.shaden.features.messaging;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.features.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessagingRestController {
    
    private final MessagingService messagingService;

    @GetMapping("/channel/{channelId}/history")
    public ResponseEntity<ResponseData> getMessageHistory(@PathVariable Long channelId) {

        ResponseData responseData = ResponseData.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Message history retrieved successfully")
                .results(messagingService.getMessageHistory(channelId))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

}
