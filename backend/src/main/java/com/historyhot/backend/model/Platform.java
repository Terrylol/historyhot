package com.historyhot.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a platform that provides trending search data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Platform {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String apiUrl;
    private boolean active;
} 