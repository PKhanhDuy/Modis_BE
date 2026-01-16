package com.example.modis.admin.controller;

import com.example.modis.user.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final UserActivityService userActivityService;
    private static final String ONLINE_USERS_KEY = "online_users";

    @GetMapping("/users/online")
    public ResponseEntity<?> countOnlineUsers() {
        long count = userActivityService.countOnlineUsers();
        return ResponseEntity.ok(Map.of(ONLINE_USERS_KEY, count));
    }
}
