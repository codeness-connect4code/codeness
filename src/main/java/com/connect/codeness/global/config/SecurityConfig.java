package com.connect.codeness.global.config;

import com.connect.codeness.global.handler.OAuth2SuccessHandler;
import com.connect.codeness.global.jwt.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	private final JwtFilter jwtFilter;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	public SecurityConfig(JwtFilter jwtFilter, OAuth2SuccessHandler oAuth2SuccessHandler) {
		this.jwtFilter = jwtFilter;
		this.oAuth2SuccessHandler = oAuth2SuccessHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		log.info("🔒 SecurityFilterChain 설정 시작");

		http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(
				auth -> auth.requestMatchers("/api/**").authenticated()
					.requestMatchers("/signup", "/login", "/api/login", "/login-page", "/users/**",
						"/payment", "/loginPage.html", "/payment.html", "/oauth2/**",
						"/login/oauth2/code/**", "/favicon.ico", "/error", "/posts/**", "/news",
						"/mentoring", "/mentoring/**", "/users/schedule").permitAll()
					.requestMatchers("/admin/**").hasAuthority("ADMIN")
					.requestMatchers(HttpMethod.POST, "/mentoring").hasAuthority("MENTOR")
					.requestMatchers(HttpMethod.DELETE, "/mentoring").hasAuthority("MENTOR")
					.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
					.anyRequest().authenticated()).exceptionHandling(
				exceptionHandling -> exceptionHandling.authenticationEntryPoint(
					(request, response, authException) -> {
						response.setContentType("application/json;charset=UTF-8");
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						String errorMessage = "{\"httpStatus\": \"UNAUTHORIZED\", \"statusCode\": 401, \"errors\": {\"UNAUTHORIZED\": \"인증이 필요합니다.\"}}";
						response.getWriter().write(errorMessage);
					})).oauth2Login(
				oauth2 -> oauth2.loginPage("/loginPage.html").successHandler(oAuth2SuccessHandler)
					.failureHandler((request, response, exception) -> {
						response.setContentType("application/json;charset=UTF-8");
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						String errorMessage =
							"{\"httpStatus\": \"UNAUTHORIZED\", \"statusCode\": 401, \"errors\": {\"OAUTH2_FAILURE\": \""
								+ exception.getMessage() + "\"}}";
						response.getWriter().write(errorMessage);
					}).userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService())))
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
		return new DefaultOAuth2UserService() {
			@Override
			public OAuth2User loadUser(OAuth2UserRequest userRequest)
				throws OAuth2AuthenticationException {
				OAuth2User user = super.loadUser(userRequest);
				log.info("🔑 OAuth2 사용자 정보 로드 성공: {}", user.getName());
				return user;
			}
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
		throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(
			List.of("http://localhost:3000", "https://codeness-front.vercel.app/"));
		configuration.setAllowedMethods(
			Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(List.of("Authorization"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		log.info("🌍 CORS 설정 완료");
		return source;
	}
}
