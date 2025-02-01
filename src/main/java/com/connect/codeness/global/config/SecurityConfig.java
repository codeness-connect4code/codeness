package com.connect.codeness.global.config;

import com.connect.codeness.global.jwt.JwtFilter;
import com.connect.codeness.global.handler.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	public SecurityConfig(JwtFilter jwtFilter, OAuth2SuccessHandler oAuth2SuccessHandler) {
		this.jwtFilter = jwtFilter;
		this.oAuth2SuccessHandler = oAuth2SuccessHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/**").authenticated()
				.requestMatchers(
					"/signup",
					"/login",
					"/api/login",
					"/login-page",
					"/users/**",
//					"/payment",
					"/loginPage.html",
//					"/payment.html",
					"/oauth2/**",
					"/login/oauth2/code/**",
					"/favicon.ico",
					"/error",
					"/posts/**",
					"/news",
					"/mentoring",
					"/mentoring/**",
					"/users/schedule"
				).permitAll()
				.requestMatchers("/admin/**").hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.POST, "/mentoring").hasAuthority("MENTOR")
				.requestMatchers(HttpMethod.DELETE, "/mentoring").hasAuthority("MENTOR")
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.authenticationEntryPoint((request, response, authException) -> {
					// JSON 형식으로 인증 실패 응답
					response.setContentType("application/json;charset=UTF-8");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

					String errorMessage = "{\"httpStatus\": \"UNAUTHORIZED\", \"statusCode\": 401, \"errors\": {\"UNAUTHORIZED\": \"인증이 필요합니다.\"}}";
					response.getWriter().write(errorMessage);
				})
			)
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/loginPage.html")
				.successHandler(oAuth2SuccessHandler)
				.failureHandler((request, response, exception) -> {
					// OAuth2 인증 실패 시 JSON 형식으로 응답
					response.setContentType("application/json;charset=UTF-8");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

					String errorMessage = "{\"httpStatus\": \"UNAUTHORIZED\", \"statusCode\": 401, \"errors\": {\"OAUTH2_FAILURE\": \"" + exception.getMessage() + "\"}}";
					response.getWriter().write(errorMessage);
				})
				.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService()))
			)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
		return new DefaultOAuth2UserService() {
			@Override
			public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
				OAuth2User user = super.loadUser(userRequest);
				// JWT 발급 후 쿠키에 저장
				String jwtToken = generateJwtToken(user);
				setJwtTokenInCookie(jwtToken);
				return user;
			}
		};
	}

	private String generateJwtToken(OAuth2User user) {
		// JWT 생성 로직 (user 정보 바탕으로 JWT를 생성)
		// 예시: return JwtUtil.generateToken(user);
		return "generated-jwt-token";  // 실제 JWT 생성 로직을 추가하세요.
	}

	private void setJwtTokenInCookie(String jwtToken) {
		HttpServletResponse response = null;  // 실제 Response 객체를 가져오는 로직 추가 필요
		response.addHeader("Set-Cookie", "access_token=" + jwtToken + "; Path=/; HttpOnly; Secure; SameSite=Strict");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000",
			"https://codeness-front.vercel.app/"
			));  // 클라이언트의 실제 URL로 수정
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);  // credentials 허용
		configuration.setExposedHeaders(List.of("Authorization"));  // Authorization 헤더 노출

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
