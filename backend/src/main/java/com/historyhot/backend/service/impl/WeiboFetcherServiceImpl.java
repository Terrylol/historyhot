package com.historyhot.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;
import com.historyhot.backend.service.PlatformFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch trending searches from Weibo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeiboFetcherServiceImpl implements PlatformFetcherService {

    private static final String PLATFORM_NAME = "weibo";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean canHandle(Platform platform) {
        return PLATFORM_NAME.equalsIgnoreCase(platform.getName());
    }

    @Override
    public List<TrendingSearch> fetchTrendingSearches(Platform platform) {
        log.info("Fetching trending searches from Weibo");
        List<TrendingSearch> trending = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DATE_FORMATTER);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(platform.getApiUrl(), String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode data = root.path("data");
                JsonNode bandList = data.path("band_list");
                
                if (bandList.isArray()) {
                    int rank = 1;
                    for (JsonNode item : bandList) {
                        String title = item.path("word").asText();
                        String url = item.path("url").asText();
                        Long hotValue = item.path("raw_hot").asLong();
                        
                        TrendingSearch search = new TrendingSearch();
                        search.setPlatform(platform);
                        search.setTitle(title);
                        search.setUrl(url);
                        search.setRank(rank++);
                        search.setHotValue(hotValue);
                        search.setFetchTime(now);
                        search.setFetchDate(today);
                        
                        trending.add(search);
                    }
                }
            } else {
                log.error("Failed to fetch Weibo trends. Response code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error fetching trending searches from Weibo: {}", e.getMessage(), e);
        }
        
        return trending;
    }
} 