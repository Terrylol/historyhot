package com.historyhot.backend.service.impl;

import com.historyhot.backend.mapper.PlatformMapper;
import com.historyhot.backend.model.Platform;
import com.historyhot.backend.service.PlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformServiceImpl implements PlatformService {

    private final PlatformMapper platformMapper;

    @Override
    public List<Platform> getAllPlatforms() {
        return platformMapper.findAll();
    }

    @Override
    public List<Platform> getActivePlatforms() {
        return platformMapper.findActivePlatforms();
    }

    @Override
    public Optional<Platform> getPlatformById(Long id) {
        return platformMapper.findById(id);
    }

    @Override
    public Optional<Platform> getPlatformByName(String name) {
        return platformMapper.findByName(name);
    }

    @Override
    public Platform savePlatform(Platform platform) {
        if (platform.getId() == null) {
            platformMapper.save(platform);
        } else {
            platformMapper.update(platform);
        }
        return platform;
    }

    @Override
    public void deletePlatform(Long id) {
        platformMapper.deleteById(id);
    }

    @Override
    public boolean togglePlatformStatus(Long id) {
        Optional<Platform> platformOpt = platformMapper.findById(id);
        if (platformOpt.isPresent()) {
            Platform platform = platformOpt.get();
            platform.setActive(!platform.isActive());
            platformMapper.update(platform);
            return platform.isActive();
        }
        return false;
    }
} 