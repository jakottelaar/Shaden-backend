package com.example.shaden.features.user.request;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {

    private Optional<String> username = Optional.empty();
    private Optional<String> email = Optional.empty();

}
