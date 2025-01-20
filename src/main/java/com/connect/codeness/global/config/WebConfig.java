package com.connect.codeness.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 요청에 대해 CORS 허용
			.allowedOrigins("http://localhost:3000") // 리액트 앱의 주소
			.allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메소드
			.allowedHeaders("*"); // 모든 헤더 허용
	}
}

