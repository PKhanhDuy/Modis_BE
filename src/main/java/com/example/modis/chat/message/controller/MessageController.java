package com.example.modis.chat.message.controller;

import com.example.modis.chat.message.dto.ListMessageDTO;
import com.example.modis.chat.message.dto.MessageDTO;
import com.example.modis.chat.message.model.Message;
import com.example.modis.chat.message.repository.MessageRepository;
import com.example.modis.chat.message.service.ListMessService;
import com.example.modis.chat.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Slf4j
public class MessageController {
    private final ListMessService listMessService;

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<ListMessageDTO>> getListMess(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        //at here I want to get list message between user and all user have conversation with user
        String senderId = authentication.getName();
        log.info("senderId:{}", senderId);

        return ResponseEntity.ok(listMessService.getRecentChatList(senderId));
    }
    @GetMapping({"/{userId}"})
    public ResponseEntity<List<MessageDTO>> getListMessByUserId(Authentication authentication, @PathVariable String userId) {
        log.info("userId:{}", userId);
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String senderId = authentication.getName();
        log.info("senderId:{}", senderId);
        List<MessageDTO> messageDTOs = messageService.getMessagesBetweenUserWithReceiver(senderId, userId);
        return ResponseEntity.ok(messageDTOs);
    }


}
