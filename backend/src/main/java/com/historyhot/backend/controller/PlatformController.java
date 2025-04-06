package com.historyhot.backend.controller;

import com.historyhot.backend.model.Platform;
import com.historyhot.backend.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping
    public ResponseEntity<List<Platform>> getAllPlatforms() {
        return ResponseEntity.ok(platformService.getAllPlatforms());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Platform>> getActivePlatforms() {
        return ResponseEntity.ok(platformService.getActivePlatforms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Platform> getPlatformById(@PathVariable Long id) {
        return platformService.getPlatformById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Platform> createPlatform(@RequestBody Platform platform) {
        return ResponseEntity.ok(platformService.savePlatform(platform));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Platform> updatePlatform(@PathVariable Long id, @RequestBody Platform platform) {
        return platformService.getPlatformById(id)
                .map(existingPlatform -> {
                    platform.setId(id);
                    return ResponseEntity.ok(platformService.savePlatform(platform));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id) {
        platformService.deletePlatform(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Boolean> togglePlatformStatus(@PathVariable Long id) {
        boolean newStatus = platformService.togglePlatformStatus(id);
        return ResponseEntity.ok(newStatus);
    }
} 