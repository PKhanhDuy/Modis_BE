package com.example.modis.admin.statistic.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyUserStat {
    private String date; // yyyy-MM-dd
    private long count;
}