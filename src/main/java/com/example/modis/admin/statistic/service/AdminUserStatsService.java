package com.example.modis.admin.statistic.service;


import com.example.modis.admin.statistic.dto.DailyUserStat;
import com.example.modis.admin.statistic.dto.UserStatsResponse;
import com.example.modis.friend.repository.FriendReqRepository;
import com.example.modis.user.enumm.Status;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserStatsService {

    private final UserRepository userRepository;
    private final FriendReqRepository friendReqRepository;

//    public UserStatsResponse getUserStats() {
//
//        long totalUsers = userRepository.count();
//        long activeUsers = userRepository.countByIsActive(Status.ACTIVE);
//        long inactiveUsers = userRepository.countByIsActive(Status.INACTIVE);
//
//        // chart: 7 ngày gần nhất
//        List<DailyUserStat> dailyStats = new ArrayList<>();
//        for (int i = 6; i >= 0; i--) {
//            LocalDate date = LocalDate.now().minusDays(i);
//            LocalDateTime start = date.atStartOfDay();
//            LocalDateTime end = date.atTime(23, 59, 59);
//
//            long count = userRepository.findByCreatedAtBetween(start, end).size();
//            dailyStats.add(new DailyUserStat(date.toString(), count));
//        }
//
//        // online (tạm thời mock)
//        long onlineUsers = Math.max(1, activeUsers / 3);
//
//        // engagement: trung bình số bạn
//        long totalFriendRelations = friendReqRepository.count();
//        double avgFriends = totalUsers == 0 ? 0 : (double) totalFriendRelations / totalUsers;
//
//        return new UserStatsResponse(
//                totalUsers,
//                activeUsers,
//                inactiveUsers,
//                dailyStats,
//                onlineUsers,
//                avgFriends
//        );
//    }
}