package com.example.shaden.features.channel;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.features.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    
    @GetMapping
    public ResponseEntity<ResponseData> getAllChannels() {
        
        ResponseData response = ResponseData.builder()
        .statusCode(HttpStatus.OK.value())
        .message("Successfully retrieved all channels")
        .results(channelService.getAllChannels())
        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
        
    }

}
