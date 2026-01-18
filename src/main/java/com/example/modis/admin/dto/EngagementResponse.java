package com.example.modis.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EngagementResponse {
    private int engagementPercent;
    private String status;
}
