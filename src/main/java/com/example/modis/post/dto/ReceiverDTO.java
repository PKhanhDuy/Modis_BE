package com.example.modis.post.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReceiverDTO {
    private String receiverId;
    private String name;
    private String avatar;
    private String icon;
    private Instant timestamp;
}
