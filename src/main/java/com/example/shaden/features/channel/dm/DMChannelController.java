package com.example.shaden.features.channel.dm;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{channelId}")
    public ResponseEntity<ResponseData> getDMChannelWithId(@PathVariable Long channelId) {
        
        ResponseData response = ResponseData.builder()
        .statusCode(HttpStatus.OK.value())
        .message("Successfully retrieved a DM channel")
        .results(dmChannelService.getDMChannelWithId(channelId))
        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseData> getDMChannelsWithUserId(@PathVariable Long userId) {
        
        ResponseData response = ResponseData.builder()
        .statusCode(HttpStatus.OK.value())
        .message("Successfully retrieved DM channel")
        .results(dmChannelService.getDMChannelsWithUserId(userId))
        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<ResponseData> deleteDMChannelWithId(@PathVariable Long channelId) {
        
        dmChannelService.deleteDMChannelWithId(channelId);

        ResponseData response = ResponseData.builder()
        .statusCode(HttpStatus.OK.value())
        .message("Successfully deleted a DM channel")
        .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

}
