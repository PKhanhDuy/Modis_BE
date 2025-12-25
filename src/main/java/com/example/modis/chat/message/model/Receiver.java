package com.example.modis.chat.message.model;

import lombok.*;

import java.time.Instant;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class Receiver {
    private String senderId;
    private String content;
    private String type;
    private Date timestamp;

    public Receiver(String senderId, String content, String type, Date timestamp) {
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Receiver(String content, Date timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public Receiver(String content, Instant timestamp) {
        this.content = content;
        if (timestamp != null) {
            this.timestamp = Date.from(timestamp);
        }
    }
}