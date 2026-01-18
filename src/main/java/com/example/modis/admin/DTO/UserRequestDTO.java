package com.example.modis.admin.DTO;

import com.example.modis.user.enumm.Role;
import com.example.modis.user.enumm.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String username;
    private String fullname;
    private String sdt;
    private String mail;
    private Role role;
    private Status isActive;
}
