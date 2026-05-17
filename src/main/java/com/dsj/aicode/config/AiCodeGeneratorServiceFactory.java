package com.dsj.aicode.config;

import com.dsj.aicode.ai.AiCodeGeneratorService;
import com.dsj.aicode.service.SystemPromptService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author dongsijun
 * @date 2026/4/11  14:58
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class AiCodeGeneratorServiceFactory {

    private final ChatModel chatModel;

    private final StreamingChatModel streamingChatModel;

    private final RedisChatMemoryStore redisChatMemoryStore;

    private final SystemPromptService systemPromptService;


    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(memoryId -> TokenWindowChatMemory
                        .builder()
                        .id(memoryId)
                        .chatMemoryStore(redisChatMemoryStore)
                        .maxTokens(170000,new OpenAiTokenCountEstimator("gpt-5"))
                        .alwaysKeepSystemMessageFirst(true)
                        .build()
                )
//                .systemMessageProvider(memoryId -> {
//                    Long appId = (Long) memoryId;
//                    String systemPrompt = systemPromptService.getSystemPromptByAppId(appId);
//                    System.out.println(systemPrompt);
//                    return systemPrompt;
//
//                })
                .build();
    }
}
