package com.dsj.aicode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 聊天模型配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "langchain4j.chat-model")
public class ChatMemoryProperties {
    private String modelName;
    private Integer maxTokens;
}