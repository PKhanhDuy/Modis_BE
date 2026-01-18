package com.example.modis.chat.websocket.service;

import com.example.modis.chat.message.mapper.ChatMessageMapper;
import com.example.modis.chat.message.model.Message;
import com.example.modis.chat.message.service.MessageService;
import com.example.modis.chat.redis.RedisMessagePublisher;
import com.example.modis.chat.message.dto.MessageDTO;
import com.example.modis.notification.dto.NotificationDTO;
import com.example.modis.notification.rabbit.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketMessageService {
    private final RedisMessagePublisher redisPublisher;
    private final MessageService messageService;
    private final ChatMessageMapper chatMessageMapper;
    private final NotificationProducer notificationProducer;

    public void sendMessageToUser(MessageDTO dto) {
        Message message = chatMessageMapper.toEntity(dto);

        Message savedMessage = messageService.save(message);
        //???? convert saved entity to DTO
        MessageDTO responseDTO = chatMessageMapper.toDTO(savedMessage);

        //publish to redis topic
        redisPublisher.publish(responseDTO);
        log.info("Sent message to User: {}", responseDTO.getSenderId());

        //Send notification to receiver
        log.info("Notified User: {}", responseDTO.getReceiverId());
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(dto.getSenderId())
                .targetId(dto.getReceiverId())
                .content(dto.getContent())
                .notificationType("MESSAGE")
                .build();
        log.info("Notification DTO: {}", notificationDTO);
        notificationProducer.sendNotification(notificationDTO);
    }

}
