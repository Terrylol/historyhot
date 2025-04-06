package com.historyhot.backend.service;

import com.historyhot.backend.model.HotSummary;
import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing hot summaries
 */
public interface HotSummaryService {
    
    /**
     * Get all hot summaries
     */
    List<HotSummary> getAllHotSummaries();
    
    /**
     * Get hot summaries for a specific date
     */
    List<HotSummary> getHotSummariesByDate(String date);
    
    /**
     * Get hot summary by id
     */
    Optional<HotSummary> getHotSummaryById(Long id);
    
    /**
     * Get hot summary by platform and date
     */
    Optional<HotSummary> getHotSummaryByPlatformAndDate(Platform platform, String date);
    
    /**
     * Save a hot summary
     */
    HotSummary saveHotSummary(HotSummary hotSummary);
    
    /**
     * Delete a hot summary
     */
    void deleteHotSummary(Long id);
    
    /**
     * Generate and save a hot summary for platform and trending searches
     * Only generates if no summary exists for the platform and date
     */
    HotSummary generateAndSaveHotSummaryIfNeeded(Platform platform, List<TrendingSearch> trendingSearches, String date);
    
    /**
     * Check if a hot summary exists for a platform and date
     */
    boolean existsHotSummaryForPlatformAndDate(Platform platform, String date);
} 