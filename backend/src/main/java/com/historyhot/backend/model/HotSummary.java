package com.historyhot.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a daily summary of trending topics with satirical content
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotSummary {
    private Long id;
    private Platform platform;
    private String content;
    private String fetchDate;
    private LocalDateTime generatedTime;
} 