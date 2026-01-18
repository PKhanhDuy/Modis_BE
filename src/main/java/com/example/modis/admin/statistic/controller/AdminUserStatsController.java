//package com.example.modis.admin.statistic.controller;
//
//
//import com.example.modis.admin.statistic.dto.UserStatsResponse;
//import com.example.modis.admin.statistic.service.AdminUserStatsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin/stats")
//@RequiredArgsConstructor
//public class AdminUserStatsController {
//
//    private final AdminUserStatsService adminUserStatsService;
//
//    @GetMapping("/users")
//    public UserStatsResponse getUserStats() {
//        return adminUserStatsService.getUserStats();
//    }
//}