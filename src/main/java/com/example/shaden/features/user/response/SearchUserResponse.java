package com.example.shaden.features.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({"user_id", "email", "username"})
public class SearchUserResponse {
    
    @JsonProperty("user_id")
    private Long id;
    private String email;
    private String username;

}
