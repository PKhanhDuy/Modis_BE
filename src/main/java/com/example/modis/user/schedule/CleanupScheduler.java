package com.example.modis.user.schedule;

import com.example.modis.user.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanupScheduler {
    private final UserActivityService userActivityService;

    @Scheduled(fixedRate = 60000)
    public void cleanInactiveUsers() {
        userActivityService.removeInactiveUsers();
    }
}
