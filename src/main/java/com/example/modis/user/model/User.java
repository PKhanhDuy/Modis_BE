package com.example.modis.user.model;

import com.example.modis.user.enumm.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Data
@Document(collection ="users")
public class User {
    @Id
    private String userId;
    private String username;
    private String pass;
    private String mail;
    private String phoneNum;
    private String avatarUrl;
    private Status isActive;
    private Role role;


    public User(String userId, String username, String pass, String mail, String phoneNum, String avatarUrl, Status isActive, Role role) {
        this.userId = userId;
        this.username = username;
        this.pass = pass;
        this.mail = mail;
        this.phoneNum = phoneNum;
        this.avatarUrl = avatarUrl;
        this.isActive = isActive;
        this.role = role;
    }
}
