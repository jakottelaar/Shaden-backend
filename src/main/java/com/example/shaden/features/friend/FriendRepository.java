package com.example.shaden.features.friend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.shaden.features.user.User;

import jakarta.transaction.Transactional;

public interface FriendRepository extends JpaRepository<Friendship, Long>{

    @Query("SELECT f FROM Friendship f " +
    "WHERE (f.sender = :user1 AND f.receiver = :user2) OR (f.sender = :user2 AND f.receiver = :user1)")
    Friendship findBySenderAndReceiver(@Param("user1") User user1, @Param("user2") User user2);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Friendship f SET f.status = ?2 WHERE f.id = ?1")
    void updateFriendShipStatus(Long id, FriendshipStatus status);

    List<Friendship> findAllBySenderAndStatus(User sender, FriendshipStatus pending);

    @Query("SELECT f FROM Friendship f WHERE (f.sender = :user OR f.receiver = :user) AND f.status = :status")
    List<Friendship> findAllFriendsByStatus(@Param("user") User user, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.sender = :user OR f.receiver = :user) AND (f.sender.id = :friendId OR f.receiver.id = :friendId)")
    Friendship findFriendByOrId(@Param("user") User user, @Param("friendId") Long friendId);

    List<Friendship> findAllBySenderAndStatusOrReceiverAndStatus(User sender, FriendshipStatus senderStatus, User receiver, FriendshipStatus receiverStatus);

}
