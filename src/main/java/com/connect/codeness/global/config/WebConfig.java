package com.connect.codeness.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 요청에 대해 CORS 허용
			.allowedOrigins("http://localhost:3000",
							"https://codeness-front.vercel.app/"
				) // 리액트 앱의 주소
			.allowedMethods("GET", "POST", "PUT", "DELETE","PATCH","OPTIONS") // 허용할 HTTP 메소드
			.allowedHeaders("*") // 모든 헤더 허용
		    .allowCredentials(true);// 클라이언트가 CORS 요청에 인증 정보를 포함할 수 있도록 허용하는 설정
	}
}

