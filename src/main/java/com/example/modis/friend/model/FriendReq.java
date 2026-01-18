package com.example.modis.friend.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "friendReq")

public class FriendReq {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String senderId;
    private String receiverId;
    private String status;
    private LocalDateTime timestamp;
}