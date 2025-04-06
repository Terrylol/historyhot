package com.historyhot.backend.controller;

import com.historyhot.backend.model.HotSummary;
import com.historyhot.backend.service.HotSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for hot summary endpoints
 */
@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class HotSummaryController {
    
    private final HotSummaryService hotSummaryService;
    
    @GetMapping
    public ResponseEntity<List<HotSummary>> getAllSummaries() {
        return ResponseEntity.ok(hotSummaryService.getAllHotSummaries());
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<HotSummary>> getSummariesByDate(@PathVariable String date) {
        return ResponseEntity.ok(hotSummaryService.getHotSummariesByDate(date));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HotSummary> getSummaryById(@PathVariable Long id) {
        return hotSummaryService.getHotSummaryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 