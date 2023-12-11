package com.example.shaden.features.channel.dm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DMChannelRepository extends JpaRepository<DMChannel, Long>{

    @Query("SELECT dm FROM DMChannel dm WHERE (dm.creator.id IN (?1, ?2) AND dm.participant.id IN (?1, ?2))")
    DMChannel findDMChannelByUserIds(Long user1Id, Long user2Id);

    @Query("SELECT dm FROM DMChannel dm WHERE dm.creator.id = ?1 OR dm.participant.id = ?1")
    List<DMChannel> findAllByUserId(long userId);

    DMChannel findDMChannelByCreatorIdOrParticipantId(Long userId, Long userId2);

}
