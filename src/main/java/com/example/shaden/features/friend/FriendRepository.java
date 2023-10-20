package com.example.shaden.features.friend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.shaden.features.user.User;

import jakarta.transaction.Transactional;

public interface FriendRepository extends JpaRepository<Friendship, Long>{

    Friendship findByFriend1AndFriend2(User user, User friend);

    List<Friendship> findAllByFriend1OrFriend2(User user, User user2);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Friendship f SET f.status = ?2 WHERE f.id = ?1")
    void updateFriendShipStatus(Long id, FriendshipStatus status);

    List<Friendship> findAllByFriend1AndStatus(User user, FriendshipStatus pending);
}
