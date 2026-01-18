package com.example.modis.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiverDTO {
    private String receiverId;
    private String name;
    private String avatar;
    private String icon;
    private Instant timestamp;
}
