package com.example.modis.user.dto;

import com.example.modis.user.enumm.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRoleRequest  {
    private Role role ;

}