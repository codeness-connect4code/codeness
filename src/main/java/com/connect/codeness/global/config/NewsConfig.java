package com.connect.codeness.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
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

		cacheManager.setCacheNames(Arrays.asList(
			"newStories","stories"
		));

		cacheManager.setCaffeine(Caffeine.newBuilder()
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.maximumSize(500));

		log.info("Initialized cache manager with caches: {}", cacheManager.getCacheNames());
		return cacheManager;
	}
}
