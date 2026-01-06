package com.example.modis.auth.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String password;
    private String fullname;
    private String mail;
    private String sdt;
    private String avatarUrl;  
    
}
