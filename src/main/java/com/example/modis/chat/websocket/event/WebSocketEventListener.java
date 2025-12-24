package com.example.modis.chat.websocket.event;


import com.example.modis.chat.message.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    private MessageDTO dto;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes == null) {
            log.warn("Session attributes are null during disconnect for sessionId: {}", accessor.getSessionId());
            return;
        }

        String userId = (String) sessionAttributes.get("userId");
        if (userId != null && !userId.trim().isEmpty()) {
            log.info("User disconnected: {}", userId);
            dto = new MessageDTO();
            dto.setSenderId(userId);
            //anyone listening to /topic/public will get this message
            //messagingTemplate.convertAndSend("/topic/public", dto);
            //send message private 1 - 1
            //redis can be used to here to get status of the user (online/offline)
            messagingTemplate.convertAndSendToUser(
                    dto.getReceiverId(),
                    "/queue/private",
                    dto
            );

        } else {
            log.warn("Username not found during disconnect for sessionId: {}", accessor.getSessionId());
        }
    }
}