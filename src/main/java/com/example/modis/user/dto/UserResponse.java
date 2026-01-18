package com.example.modis.user.dto;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @Id
    private String id;
    private String username;
    private String fullname;
    private String mail;
    private String sdt;
    private String avatarUrl;
}