package com.historyhot.backend.service;

import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;

import java.util.List;

/**
 * Interface for platform-specific trending search fetchers
 */
public interface PlatformFetcherService {
    
    /**
     * Check if this fetcher can handle the given platform
     * @param platform Platform to check
     * @return true if this fetcher can handle the platform
     */
    boolean canHandle(Platform platform);
    
    /**
     * Fetch trending searches from the platform
     * @param platform Platform to fetch from
     * @return List of trending searches
     */
    List<TrendingSearch> fetchTrendingSearches(Platform platform);
} 