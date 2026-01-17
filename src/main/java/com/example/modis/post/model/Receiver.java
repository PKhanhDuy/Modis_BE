package com.example.modis.post.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
public class Receiver {
    private String receiverId;
    private String icon;
    private Instant timestamp;

    public Receiver(String receiverId, String icon, Instant timestamp) {
        this.receiverId = receiverId;
        this.icon = icon;
        this.timestamp = timestamp;
    }
}
