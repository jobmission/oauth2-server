package com.revengemission.sso.oauth2.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CaffeineCacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        for (CachesEnum cachesEnum : CachesEnum.values()) {
            if (cachesEnum.getTtl() <= 0) {
                cacheManager.registerCustomCache(cachesEnum.name(), Caffeine.newBuilder().maximumSize(cachesEnum.getMaxSize()).build());
            } else {
                cacheManager.registerCustomCache(cachesEnum.name(), Caffeine.newBuilder().expireAfterWrite(cachesEnum.getTtl(), TimeUnit.SECONDS)
                    .maximumSize(cachesEnum.getMaxSize()).build());
            }
        }
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

}
