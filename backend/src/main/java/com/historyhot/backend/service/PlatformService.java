package com.historyhot.backend.service;

import com.historyhot.backend.model.Platform;

import java.util.List;
import java.util.Optional;

public interface PlatformService {
    
    List<Platform> getAllPlatforms();
    
    List<Platform> getActivePlatforms();
    
    Optional<Platform> getPlatformById(Long id);
    
    Optional<Platform> getPlatformByName(String name);
    
    Platform savePlatform(Platform platform);
    
    void deletePlatform(Long id);
    
    boolean togglePlatformStatus(Long id);
} 