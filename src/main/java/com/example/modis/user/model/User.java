package com.example.modis.user.model;

import com.example.modis.user.enumm.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection ="users")
public class User {
    @Id
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