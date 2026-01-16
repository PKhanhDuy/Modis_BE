package com.example.modis.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserActivityService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ONLINE_USERS_KEY = "online_users";
    private static final int ONLINE_TIMEOUT_SECONDS = 300; //5 phút

    /**
     * Ghi nhận user vừa hoạt động
     * Track mỗi khi user gọi API
     */
    public void trackUserActivity(String userId) {
        double timestamp = Instant.now().getEpochSecond();

        // ZADD online_users <timestamp> <userId>
        redisTemplate.opsForZSet().add(ONLINE_USERS_KEY, userId, timestamp);
    }

    /**
     * Đếm số user online trong khoảng ONLINE_TIMEOUT_SECONDS
     */
    public long countOnlineUsers() {
        double minTimestamp = Instant.now().getEpochSecond() - ONLINE_TIMEOUT_SECONDS;
        double maxTimestamp = Double.MAX_VALUE;

        Long count = redisTemplate.opsForZSet().count(ONLINE_USERS_KEY, minTimestamp, maxTimestamp);
        return count != null ? count : 0;
    }

    public Set<Object> getOnlineUserIds() {
        double minTimestamp = Instant.now().getEpochSecond() - ONLINE_TIMEOUT_SECONDS;
        double maxTimestamp = Double.MAX_VALUE;

        return redisTemplate.opsForZSet().rangeByScore(ONLINE_USERS_KEY, minTimestamp, maxTimestamp);
    }

    /**
     * Lập lịch xóa các user không hoạt động quá ONLINE_TIMEOUT_SECONDS ra khỏi bộ nhớ redis
     */
    public void removeInactiveUsers() {
        double now = Instant.now().getEpochSecond();
        double minScore = 0;
        double maxScore = now - ONLINE_TIMEOUT_SECONDS;

        redisTemplate.opsForZSet().removeRangeByScore(ONLINE_USERS_KEY, minScore, maxScore);
    }
}
