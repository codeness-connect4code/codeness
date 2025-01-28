package com.connect.codeness.global.config;

import com.connect.codeness.global.jwt.JwtFilter;
import com.connect.codeness.global.handler.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
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
					"/payment",
					"/loginPage.html",
					"/payment.html",
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
				.requestMatchers(HttpMethod.POST,"/mentoring").hasAuthority("MENTOR")
//				.requestMatchers(HttpMethod.GET,"/mentoring/{id}/mentoring-schedule").hasAuthority("MENTEE")
				.requestMatchers(HttpMethod.DELETE,"/mentoring").hasAuthority("MENTOR")
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
				return super.loadUser(userRequest);
			}
		};
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
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 와일드카드(*) 대신 구체적인 출처
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true); // credentials 허용
		configuration.setExposedHeaders(Arrays.asList("Authorization")); // Authorization 헤더 노출

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
