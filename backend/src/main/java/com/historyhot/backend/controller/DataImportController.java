package com.historyhot.backend.controller;

import com.historyhot.backend.util.ZhihuDataImporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制器，提供API端点来导入历史热搜数据
 */
@Slf4j
@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class DataImportController {

    private final ZhihuDataImporter zhihuDataImporter;
    
    /**
     * 导入知乎热搜数据的API端点
     * @param directoryPath 包含Markdown文件的目录路径
     * @return 导入结果
     */
    @PostMapping("/zhihu")
    public ResponseEntity<Map<String, Object>> importZhihuData(
            @RequestParam("directoryPath") String directoryPath) {
        
        log.info("收到知乎热搜数据导入请求，目录路径: {}", directoryPath);
        
        Map<String, Object> response = new HashMap<>();
        try {
            int importedCount = zhihuDataImporter.importDataFromDirectory(directoryPath);
            
            response.put("success", true);
            response.put("message", "导入成功");
            response.put("importedCount", importedCount);
            
            log.info("知乎热搜数据导入完成，共导入 {} 条记录", importedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("导入知乎热搜数据时出错: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "导入失败: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
} 