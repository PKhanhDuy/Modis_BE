package com.example.modis.chat.websocket.controller;

import com.example.modis.chat.message.dto.MessageDTO;
import com.example.modis.chat.websocket.service.WebSocketMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {
    private final WebSocketMessageService service;

    @MessageMapping("/chat.send")
    public void sendMessage(MessageDTO dto, SimpMessageHeaderAccessor headerAccessor) {
        String senderId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (senderId == null || senderId.trim().isEmpty()) {
            log.warn("Sender ID is missing in session attributes.");
            return;
        }
        dto.setSenderId(senderId);
        // Call the service to handle message sending
        service.sendMessageToUser(dto);
    }

    @GetMapping
    public ResponseEntity<?> getListMess(MessageDTO dto, SimpMessageHeaderAccessor headerAccessor) {
        //at here I want to get list message between user and all user have conversation with user
        String senderId = (String) headerAccessor.getSessionAttributes().get("userId");

        return null;
    }
}
