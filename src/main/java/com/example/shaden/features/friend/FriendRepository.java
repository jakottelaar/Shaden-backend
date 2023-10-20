package com.example.shaden.features.friend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.shaden.features.user.User;

import jakarta.transaction.Transactional;

public interface FriendRepository extends JpaRepository<Friendship, Long>{

    Friendship findByFriend1AndFriend2(User user, User friend);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Friendship f SET f.status = ?2 WHERE f.id = ?1")
    void updateFriendShipStatus(Long id, FriendshipStatus status);

    List<Friendship> findAllByFriend1AndStatus(User user, FriendshipStatus pending);

    @Query("SELECT f FROM Friendship f WHERE (f.friend1 = :user OR f.friend2 = :user) AND f.status = :status")
    List<Friendship> findAllFriendsByStatus(@Param("user") User user, @Param("status") FriendshipStatus status);
}
