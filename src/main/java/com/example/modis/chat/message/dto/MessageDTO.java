package com.example.modis.chat.message.dto;

import lombok.*;

import java.time.Instant;

@Builder
@Data
public class MessageDTO {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private Instant timestamp;

    public MessageDTO(String messageId, String senderId, String receiverId, String content, Instant timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public MessageDTO() {
    }
}
