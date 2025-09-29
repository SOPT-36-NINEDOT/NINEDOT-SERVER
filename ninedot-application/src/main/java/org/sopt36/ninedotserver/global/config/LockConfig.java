package org.sopt36.ninedotserver.global.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockConfig {

    @Bean
    public Cache<String, ReentrantLock> refreshTokenLockCache() {
        return Caffeine.newBuilder()
            .weakValues()
            .build();
    }

    @Bean
    public Cache<String, ReentrantLock> authCodeLockCache() {
        return Caffeine.newBuilder()
            .weakValues()
            .build();
    }

    @Bean
    public Cache<String, AuthResult> authResultCache() {
        return Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(1_000)
            .build();
    }
}
