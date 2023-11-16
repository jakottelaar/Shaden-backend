package com.example.shaden.features.channel.dm.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateDmChannelRequest {
    
    private Long user1Id;
    private Long user2Id;

}
