package com.historyhot.backend.service.impl;

import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;
import com.historyhot.backend.service.PlatformFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service to manage different platform fetchers and delegate calls to the appropriate one
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformFetcherManagerImpl {

    private final List<PlatformFetcherService> platformFetchers;

    /**
     * Fetch trending searches for a specific platform
     * Delegates to the appropriate fetcher service based on the platform
     * 
     * @param platform Platform to fetch from
     * @return List of trending searches or empty list if no fetcher is found
     */
    public List<TrendingSearch> fetchTrendingSearches(Platform platform) {
        for (PlatformFetcherService fetcher : platformFetchers) {
            if (fetcher.canHandle(platform)) {
                log.debug("Using fetcher {} for platform {}", 
                        fetcher.getClass().getSimpleName(), platform.getName());
                return fetcher.fetchTrendingSearches(platform);
            }
        }
        
        log.warn("No fetcher found for platform: {}", platform.getName());
        return Collections.emptyList();
    }
} 