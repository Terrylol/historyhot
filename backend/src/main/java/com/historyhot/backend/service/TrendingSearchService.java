package com.historyhot.backend.service;

import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;

import java.util.List;
import java.util.Optional;

public interface TrendingSearchService {
    
    List<TrendingSearch> getAllTrendingSearches();
    
    List<TrendingSearch> getLatestTrendingSearches();
    
    List<TrendingSearch> getTrendingSearchesByPlatform(Platform platform);
    
    List<TrendingSearch> getTrendingSearchesByPlatformAndDate(Platform platform, String date);
    
    List<TrendingSearch> getTrendingSearchesByDate(String date);
    
    List<String> getAvailableDates();
    
    Optional<TrendingSearch> getTrendingSearchById(Long id);
    
    TrendingSearch saveTrendingSearch(TrendingSearch trendingSearch);
    
    List<TrendingSearch> saveTrendingSearches(List<TrendingSearch> trendingSearches);
    
    void deleteTrendingSearch(Long id);
    
    void fetchAndSaveTrendingSearches();
} 