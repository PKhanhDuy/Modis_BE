package com.example.modis.admin.controller;

import com.example.modis.admin.dto.EngagementResponse;
import com.example.modis.user.service.UserActivityService;
import com.example.modis.user.service.UserService;
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
    private final UserService userService;
    private static final String ONLINE_USERS_KEY = "online_users";

    @GetMapping("/users/online")
    public ResponseEntity<?> countOnlineUsers() {
        long count = userActivityService.countOnlineUsers();
        return ResponseEntity.ok(Map.of(ONLINE_USERS_KEY, count));
    }

    @GetMapping("/users/total")
    public ResponseEntity<?> totalUsers() {
        long total = userService.countByRoleUser();
        return ResponseEntity.ok(Map.of("total_users", total));
    }

    @GetMapping("/users/new-today")
    public ResponseEntity<?> newUsersToday() {
        long count = userService.countNewUsersToday();
        return ResponseEntity.ok(
                Map.of("new_users_today", count)
        );
    }

    @GetMapping("/users/online/hourly")
    public ResponseEntity<?> onlineUsersByHour() {
        return ResponseEntity.ok(
                userActivityService.getOnlineUsersLastHours(6)
        );
    }

    @GetMapping("/users/engagement")
    public ResponseEntity<?> engagement() {
        long onlineUsers = userActivityService.countOnlineUsers();
        long totalUsers = userService.countByRoleUser();

        int percent = totalUsers == 0
                ? 0
                : (int) Math.round((onlineUsers * 100.0) / totalUsers);

        String status;
        if (percent >= 70) status = "Cao";
        else if (percent >= 40) status = "Trung bình";
        else status = "Thấp";

        return ResponseEntity.ok(new EngagementResponse(percent, status));
    }
}
