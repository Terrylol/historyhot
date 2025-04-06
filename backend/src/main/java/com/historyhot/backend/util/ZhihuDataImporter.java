package com.historyhot.backend.util;

import com.historyhot.backend.mapper.PlatformMapper;
import com.historyhot.backend.mapper.TrendingSearchMapper;
import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 知乎热搜数据导入工具
 * 用于从Markdown文件中导入知乎热搜数据到数据库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ZhihuDataImporter {

    private final PlatformMapper platformMapper;
    private final TrendingSearchMapper trendingSearchMapper;

    // 匹配知乎热搜条目的正则表达式
    private static final Pattern TRENDING_PATTERN = Pattern.compile(
            "\\d+\\. \\[(.*?)\\]\\((https?://www\\.zhihu\\.com/question/\\d+)\\)"
    );

    // 匹配更新时间的正则表达式
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(
            "<!-- 最后更新时间 (\\w+ \\w+ \\d+ \\d+ \\d+:\\d+:\\d+ GMT\\+\\d+ \\(.*?\\)) -->"
    );

    // 匹配文件日期的正则表达式
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "# (\\d{4}-\\d{2}-\\d{2})"
    );

    /**
     * 从指定目录导入所有Markdown文件中的知乎热搜数据
     * @param directoryPath Markdown文件所在目录的路径
     * @return 导入的记录总数
     */
    public int importDataFromDirectory(String directoryPath) {
        log.info("开始从目录导入知乎热搜数据: {}", directoryPath);
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            log.error("目录不存在或不是有效的目录: {}", directoryPath);
            return 0;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".md"));
        if (files == null || files.length == 0) {
            log.error("目录中没有找到Markdown文件: {}", directoryPath);
            return 0;
        }

        log.info("找到 {} 个Markdown文件", files.length);
        int totalImported = 0;

        for (File file : files) {
            log.info("开始处理文件: {}", file.getName());
            try {
                int importedCount = importDataFromFile(file, getOrCreateZhihuPlatform());
                totalImported += importedCount;
                log.info("文件 {} 处理完成，导入 {} 条记录", file.getName(), importedCount);
            } catch (Exception e) {
                log.error("处理文件 {} 时发生错误: {}", file.getName(), e.getMessage(), e);
            }
        }

        log.info("所有文件处理完成，共导入 {} 条记录", totalImported);
        return totalImported;
    }

    /**
     * 从单个Markdown文件导入知乎热搜数据
     * @param file Markdown文件
     * @param platform 知乎平台对象
     * @return 导入的记录数
     */
    private int importDataFromFile(File file, Platform platform) throws IOException {
        log.debug("开始读取文件内容: {}", file.getName());
        List<TrendingSearch> trendingSearches = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String content = reader.lines().reduce("", (a, b) -> a + "\n" + b);
            log.debug("文件内容读取完成，开始解析数据");

            // 解析文件日期
            String fileDate = "";
            Matcher dateMatcher = DATE_PATTERN.matcher(content);
            if (dateMatcher.find()) {
                fileDate = dateMatcher.group(1);
                log.debug("解析到文件日期: {}", fileDate);
            }

            // 解析更新时间
            LocalDateTime fetchTime = null;
            Matcher timestampMatcher = TIMESTAMP_PATTERN.matcher(content);
            if (timestampMatcher.find()) {
                String timestamp = timestampMatcher.group(1);
                // 尝试解析GMT时间格式
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z '('z')'");
                    fetchTime = LocalDateTime.parse(timestamp, formatter);
                    log.debug("解析到更新时间: {}", fetchTime);
                } catch (Exception e) {
                    log.warn("解析时间戳失败: {}, 使用文件日期作为时间", timestamp);
                    fetchTime = LocalDateTime.parse(fileDate + "T00:00:00");
                }
            } else {
                // 如果没有找到时间戳，使用文件日期
                fetchTime = LocalDateTime.parse(fileDate + "T00:00:00");
                log.warn("未找到时间戳，使用文件日期: {}", fileDate);
            }

            // 解析热搜条目
            Matcher matcher = TRENDING_PATTERN.matcher(content);
            int rank = 1;
            while (matcher.find()) {
                String title = matcher.group(1);
                String url = matcher.group(2);

                // 由于无法从文件中提取热度值，使用排名作为相对热度
                int hotValue = 10000 - (rank * 100); // 简单的热度计算方式，排名越高热度越高

                log.debug("解析到热搜条目: {} - {} 热度", title, hotValue);

                TrendingSearch trendingSearch = new TrendingSearch();
                trendingSearch.setPlatform(platform);
                trendingSearch.setTitle(title);
                trendingSearch.setUrl(url);
                trendingSearch.setHotValue((long) hotValue);
                trendingSearch.setFetchTime(fetchTime);
                trendingSearch.setFetchDate(fileDate);
                trendingSearch.setRank(rank);

                trendingSearches.add(trendingSearch);
                rank++;
            }
        }

        log.info("文件 {} 解析完成，找到 {} 条热搜记录", file.getName(), trendingSearches.size());

        if (trendingSearches.isEmpty()) {
            log.warn("文件 {} 中没有找到有效的热搜数据", file.getName());
            return 0;
        }

        // 批量保存到数据库
        int savedCount = 0;
        for (TrendingSearch trendingSearch : trendingSearches) {
            try {
                trendingSearchMapper.save(trendingSearch);
                savedCount++;
                log.debug("保存热搜记录: {}", trendingSearch.getTitle());
            } catch (Exception e) {
                log.error("保存热搜记录失败: {} - {}", trendingSearch.getTitle(), e.getMessage());
            }
        }

        log.info("文件 {} 处理完成，成功保存 {} 条记录", file.getName(), savedCount);
        return savedCount;
    }

    /**
     * 获取或创建知乎平台记录
     * @return 知乎平台对象
     */
    private Platform getOrCreateZhihuPlatform() {
        log.debug("开始获取知乎平台信息");
        Platform platform = platformMapper.findByName("zhihu").orElse(null);

        if (platform == null) {
            log.info("知乎平台不存在，创建新平台记录");
            platform = new Platform();
            platform.setName("zhihu");
            platform.setDisplayName("知乎热榜");
            platform.setApiUrl("https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=50");
            platform.setDescription("知乎热门话题");
            platform.setActive(true);
            platformMapper.save(platform);
            log.info("知乎平台创建成功");
        } else {
            log.debug("找到已存在的知乎平台记录");
        }

        return platform;
    }
} 