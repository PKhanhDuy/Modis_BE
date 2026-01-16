package com.example.modis.friend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection ="friendReq")

public class FriendReq {
@Id
@Field("_id")
private String id;
private String senderId;
private String receiverId;
private String status;
private LocalDateTime timestamp;
}
