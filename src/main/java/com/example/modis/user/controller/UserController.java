package com.example.modis.user.controller;

import com.example.modis.user.dto.UpdateMailRequest;
import com.example.modis.user.dto.UpdatePwdRequest;
import com.example.modis.user.dto.UpdateUserNameRequest;
import com.example.modis.user.dto.UpdateUserPhoneRequest;
import com.example.modis.user.dto.UpdateUserRoleRequest;
import com.example.modis.user.dto.UserResponse;
import com.example.modis.user.model.User;
import com.example.modis.user.service.UserService;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id) {
        System.out.println("Get profile "+id);
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
         

    @PutMapping("/{id}/update-phone")
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

    
    @PutMapping("/{id}/update-mail")
    public ResponseEntity<?> updateMail(@PathVariable String id,
                                        @RequestBody UpdateMailRequest request) {
        User updatedUser = userService.updatePhone(id, request.getMail());
        return ResponseEntity.ok(
                Map.of(
                        "message", "Cập nhật mail thành công",
                        "data", Map.of(
                                "message", updatedUser.getId(),
                                "sdt", updatedUser.getMail()
                        )
                )
        );
    }

    @PutMapping("/{id}/update-avatar")
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

    @PutMapping("/{id}/change-password")
        public ResponseEntity<?> changePassword(
                        @PathVariable String id,
                        @RequestBody UpdatePwdRequest request) {
                String oldPass = request.getOldPass();
                String newPass = request.getNewPass();
                String userId = userService.changePassword(id, oldPass, newPass);
                return ResponseEntity.ok(
                        Map.of(
                                "message", "Đã cập nhật mật khẩu của tài khoản",
                                "data", Map.of(
                                        "message", userId
                                )
                        )
        );
        }
}

