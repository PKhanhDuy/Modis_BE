package com.example.modis.chat.message.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ListMessageDTO {
    private String conversationId;
    private String partnerId;
    private String partnerName;
    private String partnerAvatar;
    private String lastMessage;
    private String messageType;
    private LocalDateTime timestamp;
//    private Boolean isRead;
}