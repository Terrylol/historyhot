package com.historyhot.backend.service.impl;

import com.historyhot.backend.mapper.TrendingSearchMapper;
import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;
import com.historyhot.backend.service.HotSummaryService;
import com.historyhot.backend.service.PlatformService;
import com.historyhot.backend.service.TrendingSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingSearchServiceImpl implements TrendingSearchService {

    private final TrendingSearchMapper trendingSearchMapper;
    private final PlatformService platformService;
    private final PlatformFetcherManagerImpl platformFetcherManager;
    private final HotSummaryService hotSummaryService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // 用于记录每个平台每天是否已经生成过热点总结
    private final Map<String, Map<String, Boolean>> summaryGeneratedFlags = new HashMap<>();

    @Override
    public List<TrendingSearch> getAllTrendingSearches() {
        return trendingSearchMapper.findAll();
    }

    @Override
    public List<TrendingSearch> getLatestTrendingSearches() {
        return trendingSearchMapper.findLatestTrendingSearches();
    }

    @Override
    public List<TrendingSearch> getTrendingSearchesByPlatform(Platform platform) {
        return trendingSearchMapper.findByPlatformOrderByRankAsc(platform.getId());
    }

    @Override
    public List<TrendingSearch> getTrendingSearchesByPlatformAndDate(Platform platform, String date) {
        return trendingSearchMapper.findByPlatformAndFetchDateOrderByRankAsc(platform.getId(), date);
    }

    @Override
    public List<TrendingSearch> getTrendingSearchesByDate(String date) {
        return trendingSearchMapper.findByFetchDateOrderByPlatformNameAscRankAsc(date);
    }

    @Override
    public List<String> getAvailableDates() {
        return trendingSearchMapper.findDistinctFetchDates();
    }

    @Override
    public Optional<TrendingSearch> getTrendingSearchById(Long id) {
        return Optional.ofNullable(trendingSearchMapper.findById(id));
    }

    @Override
    @Transactional
    public TrendingSearch saveTrendingSearch(TrendingSearch trendingSearch) {
        if (trendingSearch.getFetchTime() == null) {
            trendingSearch.setFetchTime(LocalDateTime.now());
        }
        
        if (trendingSearch.getFetchDate() == null) {
            trendingSearch.setFetchDate(trendingSearch.getFetchTime().format(DATE_FORMATTER));
        }
        
        // 检查并删除同一天、同一平台、相同标题的热点数据
        Long platformId = trendingSearch.getPlatform().getId();
        String fetchDate = trendingSearch.getFetchDate();
        String title = trendingSearch.getTitle();
        
        // 查找是否存在相同的数据
        List<TrendingSearch> existingSearches = trendingSearchMapper.findByPlatformAndFetchDateAndTitle(
                platformId, fetchDate, title);
        
        // 如果存在，则删除
        if (!existingSearches.isEmpty()) {
            log.info("Found existing trending search with title '{}' for platform {} on {}. Replacing with new data.", 
                    title, trendingSearch.getPlatform().getName(), fetchDate);
            trendingSearchMapper.deleteByPlatformAndFetchDateAndTitle(platformId, fetchDate, title);
        }
        
        trendingSearchMapper.save(trendingSearch);
        return trendingSearch;
    }

    @Override
    @Transactional
    public List<TrendingSearch> saveTrendingSearches(List<TrendingSearch> trendingSearches) {
        if (trendingSearches.isEmpty()) {
            return trendingSearches;
        }
        
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DATE_FORMATTER);
        
        // 获取平台信息（假设同一批次的热搜都来自同一个平台）
        Platform platform = trendingSearches.get(0).getPlatform();
        
        trendingSearches.forEach(search -> {
            if (search.getFetchTime() == null) {
                search.setFetchTime(now);
            }
            if (search.getFetchDate() == null) {
                search.setFetchDate(today);
            }
            
            // 检查并删除同一天、同一平台、相同标题的热点数据
            Long platformId = search.getPlatform().getId();
            String fetchDate = search.getFetchDate();
            String title = search.getTitle();
            
            // 查找是否存在相同的数据
            List<TrendingSearch> existingSearches = trendingSearchMapper.findByPlatformAndFetchDateAndTitle(
                    platformId, fetchDate, title);
            
            // 如果存在，则删除
            if (!existingSearches.isEmpty()) {
                log.info("Found existing trending search with title '{}' for platform {} on {}. Replacing with new data.", 
                        title, search.getPlatform().getName(), fetchDate);
                trendingSearchMapper.deleteByPlatformAndFetchDateAndTitle(platformId, fetchDate, title);
            }
        });
        
        trendingSearchMapper.saveAll(trendingSearches);
        
        // 检查今天是否已经为该平台生成过热点总结
        String platformKey = platform.getName();
        
        if (!summaryGeneratedToday(platformKey, today)) {
            // 尝试生成并保存热点总结
            hotSummaryService.generateAndSaveHotSummaryIfNeeded(platform, trendingSearches, today);
            // 标记已生成过总结
            markSummaryGenerated(platformKey, today);
        }
        
        return trendingSearches;
    }
    
    /**
     * 检查今天是否已经为指定平台生成过热点总结
     */
    private boolean summaryGeneratedToday(String platformKey, String date) {
        return summaryGeneratedFlags.containsKey(platformKey) &&
               summaryGeneratedFlags.get(platformKey).containsKey(date) &&
               summaryGeneratedFlags.get(platformKey).get(date);
    }
    
    /**
     * 标记指定平台在指定日期已生成过热点总结
     */
    private void markSummaryGenerated(String platformKey, String date) {
        summaryGeneratedFlags.computeIfAbsent(platformKey, k -> new HashMap<>())
                             .put(date, true);
    }

    @Override
    public void deleteTrendingSearch(Long id) {
        trendingSearchMapper.deleteById(id);
    }

    @Override
    @Scheduled(cron = "${scheduler.cron.fetch-trending}")
    @Transactional
    public void fetchAndSaveTrendingSearches() {
        log.info("Starting scheduled trending search fetch at {}", LocalDateTime.now());
        
        List<Platform> activePlatforms = platformService.getActivePlatforms();
        
        for (Platform platform : activePlatforms) {
            try {
                List<TrendingSearch> platformTrending = platformFetcherManager.fetchTrendingSearches(platform);
                saveTrendingSearches(platformTrending);
                log.info("Successfully fetched and saved {} trending searches from {}", 
                        platformTrending.size(), platform.getName());
            } catch (Exception e) {
                log.error("Error fetching trending searches from {}: {}", 
                        platform.getName(), e.getMessage(), e);
            }
        }
        
        log.info("Completed scheduled trending search fetch at {}", LocalDateTime.now());
    }
} 