package com.historyhot.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;
import com.historyhot.backend.service.PlatformFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch trending videos from Bilibili
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BilibiliFetcherServiceImpl implements PlatformFetcherService {

    private static final String PLATFORM_NAME = "bilibili";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // B站热门视频API URL
    private static final String BILIBILI_API_URL = "https://api.bilibili.com/x/web-interface/ranking/v2?rid=0&type=all";

    @Override
    public boolean canHandle(Platform platform) {
        return PLATFORM_NAME.equalsIgnoreCase(platform.getName());
    }

    @Override
    public List<TrendingSearch> fetchTrendingSearches(Platform platform) {
        log.info("Fetching trending videos from Bilibili");
        List<TrendingSearch> trending = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DATE_FORMATTER);

        try {
            // 设置请求头，模拟浏览器请求
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Referer", "https://www.bilibili.com/");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 使用平台配置的API URL或默认URL
            String apiUrl = platform.getApiUrl() != null ? platform.getApiUrl() : BILIBILI_API_URL;
            
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                
                // 检查API返回状态
                int code = root.path("code").asInt();
                if (code == 0) { // B站API成功返回的code为0
                    JsonNode data = root.path("data");
                    JsonNode list = data.path("list");
                    
                    if (list.isArray()) {
                        int rank = 1;
                        for (JsonNode item : list) {
                            // 限制获取前50个视频
                            if (rank > 50) break;
                            
                            String title = item.path("title").asText();
                            String bvid = item.path("bvid").asText();
                            String url = "https://www.bilibili.com/video/" + bvid;
                            String description = item.path("desc").asText();
                            Long hotValue = item.path("stat").path("view").asLong(); // 播放量作为热度值
                            
                            TrendingSearch search = new TrendingSearch();
                            search.setPlatform(platform);
                            search.setTitle(title);
                            search.setUrl(url);
                            search.setRank(rank++);
                            search.setHotValue(hotValue);
                            search.setDescription(description);
                            search.setFetchTime(now);
                            search.setFetchDate(today);
                            
                            trending.add(search);
                        }
                    }
                } else {
                    log.error("Failed to fetch Bilibili trends. API error code: {}", code);
                }
            } else {
                log.error("Failed to fetch Bilibili trends. Response code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error fetching trending videos from Bilibili: {}", e.getMessage(), e);
        }
        
        return trending;
    }
} 