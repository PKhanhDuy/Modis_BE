package com.example.modis.user.service;

import com.example.modis.admin.dto.HourlyOnlineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserActivityService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ONLINE_USERS_KEY = "online_users";
    private static final int ONLINE_TIMEOUT_SECONDS = 300; // 5 phút

    public void trackUserActivity(String userId) {
        double timestamp = Instant.now().getEpochSecond();

        redisTemplate.opsForZSet().add(ONLINE_USERS_KEY, userId, timestamp);
    }

    public long countOnlineUsers() {
        double minTimestamp = Instant.now().getEpochSecond() - ONLINE_TIMEOUT_SECONDS;
        double maxTimestamp = Double.MAX_VALUE;

        Long count = redisTemplate.opsForZSet()
                .count(ONLINE_USERS_KEY, minTimestamp, maxTimestamp);

        return count != null ? count : 0;
    }

    public Set<Object> getOnlineUserIds() {
        double minTimestamp = Instant.now().getEpochSecond() - ONLINE_TIMEOUT_SECONDS;
        double maxTimestamp = Double.MAX_VALUE;

        return redisTemplate.opsForZSet()
                .rangeByScore(ONLINE_USERS_KEY, minTimestamp, maxTimestamp);
    }

    public void removeInactiveUsers() {
        double now = Instant.now().getEpochSecond();
        double minScore = 0;
        double maxScore = now - ONLINE_TIMEOUT_SECONDS;

        redisTemplate.opsForZSet()
                .removeRangeByScore(ONLINE_USERS_KEY, minScore, maxScore);
    }

    public List<HourlyOnlineResponse> getOnlineUsersLastHours(int hours) {
        List<HourlyOnlineResponse> result = new ArrayList<>();

        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00");

        for (int i = hours - 1; i >= 0; i--) {
            Instant start = now.minusSeconds((i + 1) * 3600L);
            Instant end = now.minusSeconds(i * 3600L);
            Long count = redisTemplate.opsForZSet().count(
                    ONLINE_USERS_KEY,
                    start.getEpochSecond(),
                    end.getEpochSecond()
            );
            ZonedDateTime hourLabel = start.atZone(ZoneId.systemDefault());
            result.add(
                    new HourlyOnlineResponse(
                            hourLabel.format(formatter),
                            count == null ? 0 : count
                    )
            );
        }
        return result;
    }
}
