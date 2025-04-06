package com.historyhot.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.historyhot.backend.model.Platform;
import com.historyhot.backend.model.TrendingSearch;
import com.historyhot.backend.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AIService using OpenAI API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIServiceImpl implements AIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${openai.api.key:''}")
    private String openAiApiKey;
    
    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openAiApiUrl;
    
    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String openAiModel;
    
    @Override
    public String generateTrendingSummary(Platform platform, List<TrendingSearch> trendingSearches) {
        // 验证API密钥是否存在
        if (openAiApiKey == null || openAiApiKey.isEmpty() || "''".equals(openAiApiKey)) {
            log.warn("OpenAI API key not configured. Using mock response.");
            return generateMockResponse(platform, trendingSearches);
        }
        
        try {
            // 构建提示文本
            String platformName = platform.getDisplayName();
            String trendingItems = trendingSearches.stream()
                    .limit(20)  // 限制数量，减少token消耗
                    .map(ts -> ts.getRank() + ". " + ts.getTitle())
                    .collect(Collectors.joining("\n"));
            
            String prompt = String.format(
                    "你是一个机智幽默且略带讽刺的文化评论家。请根据以下%s榜单信息，用中文撰写一段不超过300字的辛辣点评，揭示当下社会文化现象。" +
                    "你的回应应该有趣、锐利且富有洞察力，让读者会心一笑的同时也能反思当下。\n\n" +
                    "今日%s榜单：\n%s\n\n" +
                    "记住，回复中只需包含你的评论文本，不要包含任何额外的格式或说明。",
                    platformName, platformName, trendingItems);
            
            // 构建请求正文
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", openAiModel);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);
            
            ArrayNode messagesArray = requestBody.putArray("messages");
            ObjectNode message = messagesArray.addObject();
            message.put("role", "user");
            message.put("content", prompt);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);
            
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(openAiApiUrl, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                
                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText();
                    return content;
                }
            }
            
            log.error("Failed to get response from OpenAI API: {}", response.getStatusCode());
            return generateMockResponse(platform, trendingSearches);
            
        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return generateMockResponse(platform, trendingSearches);
        }
    }
    
    /**
     * 生成模拟响应，当API调用失败时使用
     */
    private String generateMockResponse(Platform platform, List<TrendingSearch> trendingSearches) {
        String platformName = platform.getDisplayName();
        
        // 为不同平台生成不同的模拟回复
        switch (platform.getName().toLowerCase()) {
            case "weibo":
                return "今日微博热搜，依旧是\"明星绯闻+社会奇谈\"的经典配方。从某某明星的感情纠葛到网友的奇思妙想，这个信息茧房让人感叹：在微博的世界里，永远有比现实更吸引人的故事。那些真正重要的社会议题，大概只配在冷门话题区默默无闻吧。今日最佳：刷了一小时热搜，我获得了\"万事皆知，一事不精\"的超能力。";
            
            case "bilibili":
                return "B站的排行榜，见证了一代年轻人的审美变迁——从二次元文化到\"万物皆可娱乐化\"。UP主们越来越懂得如何用标题党和缩略图吸引点击，而我们这些观众则在\"信息过载\"中迷失自我。最讽刺的是，大家都在视频中寻找\"放松\"，却在不知不觉中被算法牵着鼻子走，沉浸在没完没了的短平快内容中无法自拔。";
            
            case "github":
                return "GitHub热榜：程序员们的乌托邦，今天又有多少人在Star别人的项目后永远没有使用过？那些热门仓库背后，既有改变世界的创新，也有精心包装的平庸。大多数人只是在寻找\"捷径\"——希望通过别人的代码解决自己的问题。最具讽刺性的是，这个技术乌托邦依然充斥着\"用爱发电\"的开源贡献者和永远\"收藏等于学会\"的围观群众。";
                
            default:
                return "今日热搜榜看完，突然明白了为什么古人说\"不知者无畏\"。这些引发全民关注的话题，或浅薄或荒谬，却获得了千万人的围观与热议。在信息过载的时代，我们的关注力被这些议题消耗殆尽，却很少有人能从中获得真正的智慧。这大概就是现代社会的讽刺：接收了过量的信息，却越发感到迷茫与空虚。";
        }
    }
} 