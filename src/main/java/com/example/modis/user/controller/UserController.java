package com.example.modis.user.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.modis.user.dto.UpdateUserNameRequest;
import com.example.modis.user.dto.UpdateUserPhoneRequest;
import com.example.modis.user.dto.UpdateUserRoleRequest;
import com.example.modis.user.dto.UserResponse;
import com.example.modis.user.model.User;
import com.example.modis.user.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id) {
    UserResponse userDTO = userService.getUser(id);
    return ResponseEntity.ok(
        Map.of(
            "status", "success",
            "data", userDTO
        )
    );
    }

    @PutMapping("/{id}/update-username")
    public ResponseEntity<?> updateUsername(@PathVariable String id,
                                            @RequestBody UpdateUserNameRequest request) {
        User updatedUser = userService.updateUsername(id, request.getUsername());
        return ResponseEntity.ok(
                Map.of(
                        "message", "Cập nhật tên người dùng thành công",
                        "data", Map.of(
                                "id", updatedUser.getId(),
                                "username", updatedUser.getUsername()
                        )
                )
        );
    }
         

    @PutMapping("/{id}/phone")
    public ResponseEntity<?> updatePhone(@PathVariable String id,
                                        @RequestBody UpdateUserPhoneRequest request) {
        User updatedUser = userService.updatePhone(id, request.getSdt());
        return ResponseEntity.ok(
                Map.of(
                        "message", "Cập nhật sdt thành công",
                        "data", Map.of(
                                "message", updatedUser.getId(),
                                "sdt", updatedUser.getSdt()
                        )
                )
        );
    }

    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> updateAvatar(@PathVariable String id,
                                          @RequestParam("avatar") MultipartFile avatar) {
        String secureUrl = userService.updateAvatar(id, avatar);
        return ResponseEntity.ok(
                Map.of(
                        "message", "Cập nhật avatar thành công",
                        "avatarUrl", secureUrl
                )
        );
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable String id){
        User updatedUser = userService.deleteUser(id );
        return ResponseEntity.ok(
                Map.of(
                        "message", "Đã xóa tài khoản",
                        "data", Map.of(
                                "message", updatedUser.getId()
                        )
                )
        );
    }

    @PutMapping("/{id}/update-role")
    public ResponseEntity<?> updateRole(@PathVariable String id,
                                        @RequestBody UpdateUserRoleRequest request) {
        User updatedUser = userService.updateRole(id, request.getRole());
        return ResponseEntity.ok(
                Map.of(
                        "message", "Đã cập nhật quyền của tài khoản",
                        "data", Map.of(
                                "message", updatedUser.getId(),
                                "role", updatedUser.getRole()
                        )
                )
        );
    }
}

