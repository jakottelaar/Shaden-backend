package com.example.shaden.features.channel.dm;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shaden.features.ResponseData;
import com.example.shaden.features.channel.dm.request.CreateDmChannelRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dm-channels")
@RequiredArgsConstructor
public class DMChannelController {
    
    private final DMChannelService dmChannelService;

    @PostMapping
    public ResponseEntity<ResponseData> createDMChannel(@RequestBody CreateDmChannelRequest request) {
        
        ResponseData response = ResponseData.builder()
        .statusCode(HttpStatus.CREATED.value())
        .message("Successfully created a DM channel")
        .results(dmChannelService.createDMChannel(request))
        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

}
