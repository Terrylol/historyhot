package com.historyhot.backend.service;

import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;

import java.util.List;

/**
 * Service interface for AI model interactions
 */
public interface AIService {
    
    /**
     * Generate a satirical summary of trending searches for a specific platform
     * 
     * @param platform The platform
     * @param trendingSearches List of trending searches to summarize
     * @return Generated summary text
     */
    String generateTrendingSummary(Platform platform, List<TrendingSearch> trendingSearches);
} 