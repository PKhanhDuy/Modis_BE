package com.example.modis.chat.message.model;

import lombok.*;

@Data
@Builder
public class Participants {
    private String senderId;
    private String receiverId;

    public Participants(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Participants() {
    }
}
