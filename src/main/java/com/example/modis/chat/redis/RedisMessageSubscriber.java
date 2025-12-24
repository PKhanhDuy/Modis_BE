package com.example.modis.chat.redis;

import com.example.modis.chat.message.mapper.ChatMessageMapper;
import com.example.modis.chat.message.model.Message;
import com.example.modis.chat.message.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageMapper chatMessageMapper;

    public void onMessage(Message message) {
        try {
            MessageDTO messageDTO = chatMessageMapper.toDTO(message);
            log.info("Received message from Redis: {}", messageDTO.getSenderId(), messageDTO.getReceiverId());

            messagingTemplate.convertAndSendToUser(messageDTO.getReceiverId(), "/queue/messages", messageDTO);

            messagingTemplate.convertAndSendToUser(messageDTO.getSenderId(), "/queue/messages", messageDTO);
        }catch (Exception e){
            log.error("Error processing message from Redis: {}", e.getMessage());
        }
    }
}
