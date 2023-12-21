package com.example.shaden.features.messaging;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>{
    
    List<Message> findAllByChannelIdOrderByCreatedDateAsc(Long channelId);

}
