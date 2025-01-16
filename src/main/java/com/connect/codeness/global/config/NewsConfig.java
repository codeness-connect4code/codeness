package com.connect.codeness.global.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableCaching
@Slf4j
public class NewsConfig {

	@Bean
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(5000);
		factory.setReadTimeout(5000);

		return new RestTemplate(factory);
	}

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();

		Cache<Object, Object> newStoriesCache = Caffeine.newBuilder()
				.expireAfterAccess(2,TimeUnit.MINUTES)
				.expireAfterWrite(5,TimeUnit.MINUTES)
				.maximumSize(1)
				.build();

		Cache<Object, Object> storiesCache = Caffeine.newBuilder()
			.expireAfterWrite(30,TimeUnit.MINUTES)
			.maximumSize(200)
			.build();

		cacheManager.registerCustomCache("newStories", newStoriesCache);
		cacheManager.registerCustomCache("stories", storiesCache);

		log.info("Initialized cache manager with caches: {}", cacheManager.getCacheNames());
		return cacheManager;
	}
}
