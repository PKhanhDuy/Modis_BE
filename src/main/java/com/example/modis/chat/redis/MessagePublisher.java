package com.example.modis.chat.redis;

import com.example.modis.chat.message.dto.MessageDTO;

public interface MessagePublisher {
    void publish(MessageDTO message);
}
