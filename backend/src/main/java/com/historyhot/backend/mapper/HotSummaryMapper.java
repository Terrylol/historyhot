package com.historyhot.backend.mapper;

import com.historyhot.backend.model.HotSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HotSummaryMapper {
    
    List<HotSummary> findAll();
    
    HotSummary findById(@Param("id") Long id);
    
    HotSummary findByPlatformAndFetchDate(
            @Param("platformId") Long platformId, 
            @Param("fetchDate") String fetchDate);
    
    List<HotSummary> findByFetchDate(@Param("fetchDate") String fetchDate);
    
    void save(HotSummary hotSummary);
    
    void deleteById(@Param("id") Long id);
    
    boolean existsByPlatformAndFetchDate(
            @Param("platformId") Long platformId, 
            @Param("fetchDate") String fetchDate);
} 