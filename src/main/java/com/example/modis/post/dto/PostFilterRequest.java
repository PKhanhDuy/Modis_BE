package com.example.modis.post.dto;

import lombok.Data;

@Data
public class PostFilterRequest {
    // Các tham số lọc
    private String userId;
    private String type = "ALL";        // Mặc định là ALL
    private String senderId;            // Có thể null
    private String viewMode = "LIST";   // LIST hoặc GRID

    // Các tham số phân trang
    private int page = 0;               // Mặc định trang 0
    private int size = 50;              // Mặc định 50 bài/trang

    public String toRedisKey() {
        return String.format("posts:%s:%s:%s:%s:%d:%d",
                userId,
                type,
                viewMode,
                (senderId == null ? "null" : senderId),
                page,
                size
        );
    }
}

