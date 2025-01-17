package com.connect.codeness.global.config;

import com.connect.codeness.global.jwt.JwtFilter;
import com.connect.codeness.global.handler.OAuth2SuccessHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
							"/signup",
							"/login",
							"/api/**",
							"/login-page",
							"/users/**",
							"/payment",
							"/mentoring/**",
							"/loginPage.html",
							"/payment.html",
							"/oauth2/**",
							"/login/oauth2/code/**",
							"/favicon.ico",
							"/error",
							"/posts/**",
							"/news"
				).permitAll()
				.requestMatchers("/admin/**").hasAuthority("ADMIN")
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> {
				oauth2.loginPage("/loginPage.html")
					.successHandler(oAuth2SuccessHandler)
					.failureHandler((request, response, exception) -> {
						response.sendRedirect("/loginPage.html?error=" + exception.getMessage());
					})
					.userInfoEndpoint(userInfo ->
						userInfo.userService(customOAuth2UserService()));
			})
			//.addFilterAfter(jwtFilter, ExceptionTranslationFilter.class);
		//시큐리티 예외처리 필터
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
		return new DefaultOAuth2UserService() {
			@Override
			public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
				OAuth2User user = super.loadUser(userRequest);
				return user;
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
}