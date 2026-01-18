package com.example.modis.config;

import com.example.modis.user.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserActivityInterceptor implements HandlerInterceptor {
    private final UserActivityService userActivityService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = getCurrentUserId();

        if (userId != null) {
            userActivityService.trackUserActivity(userId);
        }
        return true;
    }

    private String getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        return principal instanceof String ? (String) principal : null;
    }
}
