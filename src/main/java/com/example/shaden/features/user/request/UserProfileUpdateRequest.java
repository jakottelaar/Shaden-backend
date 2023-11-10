package com.example.shaden.features.user.request;

import java.util.Optional;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {

    private Optional<String> username = Optional.empty();
    private Optional<String> email = Optional.empty();

}
