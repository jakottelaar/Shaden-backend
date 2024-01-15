package com.example.shaden.features.messaging;

import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.features.channel.Channel;
import com.example.shaden.features.channel.ChannelRepository;
import com.example.shaden.features.messaging.request.MessageRequest;
import com.example.shaden.features.messaging.response.MessageResponse;
import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {

  private final MessageRepository messageRepository;

  private final ChannelRepository channelRepository;

  private final UserRepository userRepository;

  private static final Logger LOG = LoggerFactory.getLogger(
    MessagingService.class
  );

  public MessageResponse saveMessage(MessageRequest messageRequest) {
    Channel channel = channelRepository
      .findById(messageRequest.getChannelId())
      .orElseThrow(() -> new ResourceNotFoundException("Channel not found"));

    User sender = userRepository
      .findById(messageRequest.getSenderId())
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Message message = Message
      .builder()
      .channel(channel)
      .sender(sender)
      .content(messageRequest.getContent())
      .createdDate(LocalDateTime.now())
      .lastModifiedDate(LocalDateTime.now())
      .build();

    messageRepository.save(message);

    MessageResponse messageResponse = mapMessageToMessageResponse(message);

    return messageResponse;
  }

  public List<MessageResponse> getMessageHistory(Long channelId) {
    List<Message> messages = messageRepository.findAllByChannelIdOrderByCreatedDateAsc(
      channelId
    );

    if (messages.isEmpty()) {
      throw new ResourceNotFoundException("No messages found");
    }

    List<MessageResponse> messageResponses = messages
      .stream()
      .map(this::mapMessageToMessageResponse)
      .toList();

    return messageResponses;
  }

  private MessageResponse mapMessageToMessageResponse(Message message) {
    return MessageResponse
      .builder()
      .messageId(message.getId())
      .channelId(message.getChannel().getId())
      .senderUsername(message.getSender().getUsername())
      .senderId(message.getSender().getId())
      .content(message.getContent())
      .createdDate(message.getCreatedDate().toString())
      .lastModifiedDate(message.getLastModifiedDate().toString())
      .build();
  }

  public void deleteMessage(Long messageId, Long channelId) {
    Message message = messageRepository
      .findByIdAndChannelId(messageId, channelId)
      .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

    messageRepository.delete(message);
  }
}
