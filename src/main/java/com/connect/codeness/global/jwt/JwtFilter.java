package com.connect.codeness.global.jwt;

import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN;
import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN_EXPIRATION;
import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;
import static com.connect.codeness.global.constants.Constants.BEARER;
import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN;
import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN_EXPIRATION;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private static final List<String> POST_EXCLUDED_PATHS = List.of("/login", "/signup", "/logout");
	private static final List<String> GET_EXCLUDED_PATHS = List.of("/posts", "/posts/.*", "/news",
		"/mentoring/\\d+/reviews", "/mentoring", "/mentoring.*", "/users/schedule");
	private final JwtProvider jwtProvider;
	public JwtFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain) throws ServletException, IOException {

		String requestPath = request.getRequestURI();
		String method = request.getMethod();

		// 특정 경로에서는 필터 적용 제외
		if (isExcludedPath(requestPath, method)) {
			chain.doFilter(request, response);
			return;
		}

		// Access Token과 Refresh Token을 가져오기
		String accessToken = request.getHeader(AUTHORIZATION);
		if (accessToken != null && accessToken.startsWith(BEARER)) {
			accessToken = accessToken.substring(BEARER.length());
		}
		String refreshToken = getCookie(request, REFRESH_TOKEN);

		try {
			if (accessToken != null) {
				// Access Token이 유효한 경우, 인증 정보 설정
				if (jwtProvider.validationAccessToken(accessToken)) {
					setAuthentication(accessToken, request);
				}
				// Access Token이 만료되었고 Refresh Token이 유효한 경우, 새로운 Access Token 발급
				else if (refreshToken != null && jwtProvider.validationRefreshToken(refreshToken)) {
					String email = jwtProvider.extractEmail(refreshToken);
					String role = jwtProvider.extractRole(refreshToken);
					Long userId = jwtProvider.extractUserId(refreshToken);
					String provider = jwtProvider.extractProvider(refreshToken);

					// 새 Access Token 발급
					String newAccessToken = jwtProvider.regenerateAccessToken(refreshToken, email,
						role, provider);

					// 응답 헤더에 새 Access Token 설정
					response.setHeader(AUTHORIZATION, BEARER + newAccessToken);

					// 새 Access Token을 사용하여 인증 정보 설정
					setAuthentication(newAccessToken, request);

					// 새로운 Refresh Token 발급 및 설정
					String newRefreshToken = jwtProvider.generateRefreshToken(userId);
					response.addCookie(
						jwtProvider.createHttpOnlyCookie(REFRESH_TOKEN, newRefreshToken,
							REFRESH_TOKEN_EXPIRATION));
				}
			}
		} catch (Exception e) {
			log.error("Cannot set user authentication: {}", e.getMessage());
		}

		chain.doFilter(request, response);
	}

	private void setAuthentication(String token, HttpServletRequest request) {
		String email = jwtProvider.extractEmail(token);
		String role = jwtProvider.extractRole(token);

		if (role != null) {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				email, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("Authentication set for user: {}", email);
		}
	}

	private boolean isExcludedPath(String path, String method) {
		if ("GET".equalsIgnoreCase(method) && GET_EXCLUDED_PATHS.stream().anyMatch(path::matches)) {
			return true;
		}
		return "POST".equalsIgnoreCase(method) && POST_EXCLUDED_PATHS.stream()
			.anyMatch(path::matches);
	}

	private String getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
