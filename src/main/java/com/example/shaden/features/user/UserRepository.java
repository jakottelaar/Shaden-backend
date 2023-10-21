package com.example.shaden.features.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shaden.features.user.response.SearchUserResponse;

public interface UserRepository extends JpaRepository<User, Long>{
    
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<SearchUserResponse> findByUsernameContaining(String username);

}
