package com.dsj.aicode.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dongsijun
 * @date 2026/5/14  10:11
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class ChatMemoryStoreConfig {

    private String host;

    private Integer port;

    private String password;

    private long ttl;

    @Bean
    public RedisChatMemoryStore redisChatMemoryStore() {
        return RedisChatMemoryStore.builder()
                .host(host)
                .port(port)
                .ttl(ttl)
                .build();
    }
}
