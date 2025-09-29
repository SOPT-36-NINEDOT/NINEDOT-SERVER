package org.sopt36.ninedotserver.global.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockConfig {

    @Bean
    public Cache<String, ReentrantLock> refreshTokenLockCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .maximumSize(100_000)
            .build();
    }

    @Bean
    public Cache<String, ReentrantLock> authCodeLockCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .maximumSize(100_000)
            .build();
    }
}
