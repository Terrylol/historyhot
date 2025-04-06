package com.historyhot.backend.mapper;

import com.historyhot.backend.model.Platform;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PlatformMapper {
    
    List<Platform> findAll();
    
    List<Platform> findActivePlatforms();
    
    Optional<Platform> findById(@Param("id") Long id);
    
    Optional<Platform> findByName(@Param("name") String name);
    
    void save(Platform platform);
    
    void update(Platform platform);
    
    void deleteById(@Param("id") Long id);
} 