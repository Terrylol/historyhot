package com.historyhot.backend.service.impl;

import com.historyhot.backend.mapper.HotSummaryMapper;
import com.historyhot.backend.model.HotSummary;
import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;
import com.historyhot.backend.service.AIService;
import com.historyhot.backend.service.HotSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of HotSummaryService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HotSummaryServiceImpl implements HotSummaryService {

    private final HotSummaryMapper hotSummaryMapper;
    private final AIService aiService;

    @Override
    public List<HotSummary> getAllHotSummaries() {
        return hotSummaryMapper.findAll();
    }

    @Override
    public List<HotSummary> getHotSummariesByDate(String date) {
        return hotSummaryMapper.findByFetchDate(date);
    }

    @Override
    public Optional<HotSummary> getHotSummaryById(Long id) {
        return Optional.ofNullable(hotSummaryMapper.findById(id));
    }

    @Override
    public Optional<HotSummary> getHotSummaryByPlatformAndDate(Platform platform, String date) {
        return Optional.ofNullable(hotSummaryMapper.findByPlatformAndFetchDate(platform.getId(), date));
    }

    @Override
    @Transactional
    public HotSummary saveHotSummary(HotSummary hotSummary) {
        if (hotSummary.getGeneratedTime() == null) {
            hotSummary.setGeneratedTime(LocalDateTime.now());
        }
        hotSummaryMapper.save(hotSummary);
        return hotSummary;
    }

    @Override
    @Transactional
    public void deleteHotSummary(Long id) {
        hotSummaryMapper.deleteById(id);
    }

    @Override
    @Transactional
    public HotSummary generateAndSaveHotSummaryIfNeeded(Platform platform, List<TrendingSearch> trendingSearches, String date) {
        // 检查今天是否已经为此平台生成过总结
        if (existsHotSummaryForPlatformAndDate(platform, date)) {
            log.info("Hot summary for platform {} on {} already exists, skipping generation", 
                    platform.getName(), date);
            return hotSummaryMapper.findByPlatformAndFetchDate(platform.getId(), date);
        }
        
        try {
            // 通过AI服务生成讽刺性总结
            log.info("Generating hot summary for platform {} on {}", platform.getName(), date);
            String summaryContent = aiService.generateTrendingSummary(platform, trendingSearches);
            
            // 创建并保存热点总结
            HotSummary summary = new HotSummary();
            summary.setPlatform(platform);
            summary.setContent(summaryContent);
            summary.setFetchDate(date);
            summary.setGeneratedTime(LocalDateTime.now());
            
            return saveHotSummary(summary);
        } catch (Exception e) {
            log.error("Error generating hot summary for platform {} on {}: {}", 
                    platform.getName(), date, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean existsHotSummaryForPlatformAndDate(Platform platform, String date) {
        return hotSummaryMapper.existsByPlatformAndFetchDate(platform.getId(), date);
    }
} 