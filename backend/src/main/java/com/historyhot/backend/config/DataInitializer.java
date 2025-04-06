package com.historyhot.backend.config;

import com.historyhot.backend.model.Platform;
import com.historyhot.backend.service.PlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Initialize default data when the application starts
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PlatformService platformService;

    @Override
    public void run(String... args) {
        log.info("Initializing default platforms...");
        
        // Define all platforms in a structured way
        List<Platform> defaultPlatforms = Arrays.asList(
            createPlatform(
                "weibo", 
                "微博热搜", 
                "https://weibo.com/ajax/side/hotSearch", 
                "新浪微博热搜榜单", 
                true
            ),
            createPlatform(
                "baidu", 
                "百度热搜", 
                "https://top.baidu.com/api/board?platform=wise&tab=realtime", 
                "百度搜索风云榜", 
                true
            ),
            createPlatform(
                "zhihu", 
                "知乎热榜", 
                "https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=50", 
                "知乎热门话题", 
                true
            ),
            createPlatform(
                "github", 
                "GitHub热门", 
                "https://api.github.com/search/repositories?q=stars:>1&sort=stars&order=desc", 
                "GitHub 热门项目榜单", 
                true
            ),
            createPlatform(
                "bilibili", 
                "B站热门", 
                "https://api.bilibili.com/x/web-interface/ranking/v2?rid=0&type=all", 
                "哔哩哔哩热门视频榜单", 
                true
            )
        );
        
        // Initialize all platforms
        for (Platform platform : defaultPlatforms) {
            String name = platform.getName();
            if (platformService.getPlatformByName(name).isEmpty()) {
                platformService.savePlatform(platform);
                log.info("Added {} platform", name);
            }
        }
        
        log.info("Platform initialization completed");
    }
    
    /**
     * Helper method to create a platform
     */
    private Platform createPlatform(String name, String displayName, String apiUrl, String description, boolean active) {
        Platform platform = new Platform();
        platform.setName(name);
        platform.setDisplayName(displayName);
        platform.setApiUrl(apiUrl);
        platform.setDescription(description);
        platform.setActive(active);
        return platform;
    }
} 