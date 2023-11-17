package com.example.shaden.features.channel.dm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DMChannelRepository extends JpaRepository<DMChannel, Long>{

    @Query("SELECT dm FROM DMChannel dm WHERE (dm.user1.id = ?1 AND dm.user2.id = ?2) OR (dm.user1.id = ?2 AND dm.user2.id = ?1)")
    DMChannel findDMChannelByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Query("SELECT dm FROM DMChannel dm WHERE dm.user1.id = ?1 OR dm.user2.id = ?1")
    List<DMChannel> findAllByUserId(long userId);

}
