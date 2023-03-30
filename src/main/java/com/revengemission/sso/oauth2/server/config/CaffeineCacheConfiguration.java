package com.revengemission.sso.oauth2.server.config;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CaffeineCacheConfiguration {

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		ArrayList<CaffeineCache> caches = new ArrayList<>();
		for (CachesEnum c : CachesEnum.values()) {
			caches.add(new CaffeineCache(c.name(), Caffeine.newBuilder().expireAfterWrite(c.getTtl(), TimeUnit.SECONDS)
					.maximumSize(c.getMaxSize()).build()));
		}
		cacheManager.setCaches(caches);
		return cacheManager;
	}

}
