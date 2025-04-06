package com.historyhot.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a trending search from a specific platform at a specific time
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendingSearch {
    private Long id;
    private Platform platform;
    private String title;
    private String url;
    private Integer rank;
    private Long hotValue;
    private String description;
    private LocalDateTime fetchTime;
    private String fetchDate;
} 