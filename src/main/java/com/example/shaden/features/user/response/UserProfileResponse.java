package com.example.shaden.features.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    
    private String email;
    private String username;

}
