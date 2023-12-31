package com.example.shaden.features.statistics;

import org.springframework.stereotype.Service;

import com.example.shaden.features.friend.FriendRepository;
import com.example.shaden.features.messaging.MessageRepository;
import com.example.shaden.features.statistics.response.StatisticsResponse;
import com.example.shaden.features.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final MessageRepository messageRepository;

    public StatisticsResponse getAllStatistics() {
        
        Long totalUsers = userRepository.count();
        Long totalFriendships = friendRepository.count();
        Long totalMessages = messageRepository.count();

        return StatisticsResponse.builder()
                .totalUsers(totalUsers)
                .totalFriendships(totalFriendships)
                .totalMessages(totalMessages)
                .build();
    }

}
