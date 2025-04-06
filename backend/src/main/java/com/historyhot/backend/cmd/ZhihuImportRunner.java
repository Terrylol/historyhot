package com.historyhot.backend.cmd;

import com.historyhot.backend.util.ZhihuDataImporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 命令行运行器，用于导入知乎热搜数据
 * 通过设置spring.zhihu.import.enabled=true来启用
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.zhihu.import.enabled", havingValue = "true")
public class ZhihuImportRunner implements CommandLineRunner {

    private final ZhihuDataImporter zhihuDataImporter;
    
    @Override
    public void run(String... args) {
        log.info("开始执行知乎热搜数据导入...");
        
        String directoryPath = System.getProperty("zhihu.import.directory");
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            log.error("请使用-Dzhihu.import.directory=目录路径 指定知乎热搜数据目录");
            return;
        }
        
        int importedCount = zhihuDataImporter.importDataFromDirectory(directoryPath);
        log.info("知乎热搜数据导入完成，共导入 {} 条记录", importedCount);
    }
} 