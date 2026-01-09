package com.example.modis.admin.statistic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;

    // user mới theo ngày (chart)
    private List<DailyUserStat> dailNewUsers;

    // online giả lập / realtime sau này
    private long onlineUsers;

    // engagement
    private double avgFriendsPerUser;
}
