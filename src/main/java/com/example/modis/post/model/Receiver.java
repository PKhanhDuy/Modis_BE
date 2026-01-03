package com.example.modis.post.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
public class Receiver {
    private String receiverId;
    private String icon;
    private Date timestamp;

    public Receiver(String receiverId, String icon, Date timestamp) {
        this.receiverId = receiverId;
        this.icon = icon;
        this.timestamp = timestamp;
    }
}
