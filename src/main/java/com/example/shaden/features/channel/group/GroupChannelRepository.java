package com.example.shaden.features.channel.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupChannelRepository extends JpaRepository<GroupChannel, Long>{

    @Query("SELECT DISTINCT gc FROM GroupChannel gc LEFT JOIN gc.users AS u WHERE gc.creator.id = ?1 OR ?1 IN (SELECT u.id FROM gc.users)")
    List<GroupChannel> findAllByUserId(long userId);

}
