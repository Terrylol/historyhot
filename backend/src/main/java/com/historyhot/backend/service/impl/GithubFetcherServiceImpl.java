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
 * Service to fetch trending repositories from GitHub
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GithubFetcherServiceImpl implements PlatformFetcherService {

    private static final String PLATFORM_NAME = "github";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // GitHub API URL for trending repositories
    private static final String GITHUB_API_URL = "https://api.github.com/search/repositories?q=stars:>1&sort=stars&order=desc";

    @Override
    public boolean canHandle(Platform platform) {
        return PLATFORM_NAME.equalsIgnoreCase(platform.getName());
    }

    @Override
    public List<TrendingSearch> fetchTrendingSearches(Platform platform) {
        log.info("Fetching trending repositories from GitHub");
        List<TrendingSearch> trending = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DATE_FORMATTER);

        try {
            // Set up headers for GitHub API
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github.v3+json");
            // If you have a GitHub token, you can add it here to avoid rate limiting
            // headers.set("Authorization", "token YOUR_GITHUB_TOKEN");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Use the platform's API URL if available, otherwise use the default
            String apiUrl = platform.getApiUrl() != null ? platform.getApiUrl() : GITHUB_API_URL;
            
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode items = root.path("items");
                
                if (items.isArray()) {
                    int rank = 1;
                    for (JsonNode item : items) {
                        if (rank > 50) break; // Limit to top 50 repositories
                        
                        String repoName = item.path("full_name").asText();
                        String repoUrl = item.path("html_url").asText();
                        String description = item.path("description").asText();
                        Long stars = item.path("stargazers_count").asLong();
                        
                        TrendingSearch search = new TrendingSearch();
                        search.setPlatform(platform);
                        search.setTitle(repoName);
                        search.setUrl(repoUrl);
                        search.setRank(rank++);
                        search.setHotValue(stars);
                        search.setDescription(description);
                        search.setFetchTime(now);
                        search.setFetchDate(today);
                        
                        trending.add(search);
                    }
                }
            } else {
                log.error("Failed to fetch GitHub trends. Response code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error fetching trending repositories from GitHub: {}", e.getMessage(), e);
        }
        
        return trending;
    }
} 