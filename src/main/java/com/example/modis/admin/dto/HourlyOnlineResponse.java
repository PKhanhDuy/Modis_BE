package com.example.modis.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HourlyOnlineResponse {
    private String hour;
    private long count;
}
