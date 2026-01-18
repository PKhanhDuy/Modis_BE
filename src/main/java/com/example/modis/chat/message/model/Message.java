package com.example.modis.chat.message.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    private String messageId;
    private Participants participants;
    private Receiver receiver;

    public static void main(String[] args) {
        System.out.println("Hello World and Happy New Year 2026!");
    }

}
