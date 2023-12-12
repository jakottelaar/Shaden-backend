package com.example.shaden.features.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

    @JsonProperty("user_id")
    private Long userId;
    private String email;
    private String username;

}
