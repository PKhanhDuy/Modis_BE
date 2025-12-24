package com.example.modis.chat.message.mapper;

import com.example.modis.chat.message.model.Message;
import com.example.modis.chat.message.model.Participants;
import com.example.modis.chat.message.model.Receiver;
import com.example.modis.chat.message.dto.MessageDTO;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ChatMessageMapper {
    public Message toEntity(MessageDTO dto) {
        if (dto == null) {
            return null;
        }

        Date timestamp = (dto.getTimestamp() != null)
                ? Date.from(dto.getTimestamp())
                : new Date();

        return Message.builder()
                .participants(new Participants(dto.getSenderId(), dto.getReceiverId()))
                .receiver(new Receiver(dto.getContent(), timestamp.toInstant()))
                .build();
    }

    public MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }

        Date entityTimestamp = message.getReceiver().getTimestamp();

        return MessageDTO.builder()
                .messageId(message.getMessageId())
                .senderId(message.getParticipants().getSenderId())
                .receiverId(message.getParticipants().getReceiverId())
                .content(message.getReceiver().getContent())
                .timestamp(entityTimestamp != null ? entityTimestamp.toInstant() : null)
                .build();
    }
}
