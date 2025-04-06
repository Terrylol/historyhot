package com.historyhot.backend.mapper;

import com.historyhot.backend.model.TrendingSearch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TrendingSearchMapper {
    
    List<TrendingSearch> findAll();
    
    List<TrendingSearch> findLatestTrendingSearches();
    
    List<TrendingSearch> findByPlatformOrderByRankAsc(@Param("platformId") Long platformId);
    
    List<TrendingSearch> findByPlatformAndFetchDateOrderByRankAsc(
            @Param("platformId") Long platformId, 
            @Param("fetchDate") String fetchDate);
    
    List<String> findDistinctFetchDates();
    
    List<TrendingSearch> findByFetchDateOrderByPlatformNameAscRankAsc(@Param("fetchDate") String fetchDate);
    
    TrendingSearch findById(@Param("id") Long id);
    
    void save(TrendingSearch trendingSearch);
    
    void saveAll(@Param("list") List<TrendingSearch> trendingSearches);
    
    void deleteById(@Param("id") Long id);
    
    List<TrendingSearch> findByFetchTimeBetweenOrderByFetchTimeDesc(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    List<TrendingSearch> findByPlatformAndFetchDateAndTitle(
            @Param("platformId") Long platformId,
            @Param("fetchDate") String fetchDate,
            @Param("title") String title);
    
    void deleteByPlatformAndFetchDateAndTitle(
            @Param("platformId") Long platformId,
            @Param("fetchDate") String fetchDate,
            @Param("title") String title);
} 