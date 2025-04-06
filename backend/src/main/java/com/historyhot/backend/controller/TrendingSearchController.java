package com.historyhot.backend.controller;

import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;
import com.historyhot.backend.service.PlatformService;
import com.historyhot.backend.service.TrendingSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trending")
@RequiredArgsConstructor
public class TrendingSearchController {

    private final TrendingSearchService trendingSearchService;
    private final PlatformService platformService;

    @GetMapping("/latest")
    public ResponseEntity<List<TrendingSearch>> getLatestTrendingSearches() {
        return ResponseEntity.ok(trendingSearchService.getLatestTrendingSearches());
    }

    @GetMapping("/dates")
    public ResponseEntity<List<String>> getAvailableDates() {
        return ResponseEntity.ok(trendingSearchService.getAvailableDates());
    }

    @GetMapping("/platform/{platformId}")
    public ResponseEntity<List<TrendingSearch>> getTrendingSearchesByPlatform(@PathVariable Long platformId) {
        return platformService.getPlatformById(platformId)
                .map(platform -> ResponseEntity.ok(trendingSearchService.getTrendingSearchesByPlatform(platform)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/platform/{platformId}/date/{date}")
    public ResponseEntity<List<TrendingSearch>> getTrendingSearchesByPlatformAndDate(
            @PathVariable Long platformId, @PathVariable String date) {
        return platformService.getPlatformById(platformId)
                .map(platform -> ResponseEntity.ok(
                        trendingSearchService.getTrendingSearchesByPlatformAndDate(platform, date)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TrendingSearch>> getTrendingSearchesByDate(@PathVariable String date) {
        return ResponseEntity.ok(trendingSearchService.getTrendingSearchesByDate(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrendingSearch> getTrendingSearchById(@PathVariable Long id) {
        return trendingSearchService.getTrendingSearchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/fetch")
    public ResponseEntity<String> triggerFetch() {
        trendingSearchService.fetchAndSaveTrendingSearches();
        return ResponseEntity.ok("Trending search fetch triggered successfully");
    }
} 