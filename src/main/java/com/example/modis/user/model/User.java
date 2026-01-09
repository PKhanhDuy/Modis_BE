package com.example.modis.user.model;

import com.example.modis.user.enumm.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection ="users")
public class User {
    @Id
    @Field("_id")
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String mail;
    private String sdt;
    private String avatarUrl;
    private Status isActive;
    private Role role;
    private LocalDateTime createdAt;
}