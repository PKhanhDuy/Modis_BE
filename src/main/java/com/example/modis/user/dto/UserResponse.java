package com.example.modis.user.dto;
import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class UserResponse {
    @Id
    private String id;
    private String username;
    private String fullname;
    private String mail;
    private String sdt;
    private String avatarUrl;
}
