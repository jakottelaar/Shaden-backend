package com.example.shaden.features.friend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shaden.features.user.User;

public interface FriendRepository extends JpaRepository<Friendship, Long>{

    Friendship findByFriend1AndFriend2(User user, User friend);

    List<Friendship> findAllByFriend1OrFriend2(User user, User user2);
    
}
