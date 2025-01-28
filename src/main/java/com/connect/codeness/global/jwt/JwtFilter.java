package com.connect.codeness.global.jwt;

import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN_EXPIRATION;
import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN;

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
	private final JwtProvider jwtProvider;

	public JwtFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	// 제외할 경로
	private static final List<String> POST_EXCLUDED_PATHS = List.of("/login", "/signup", "/logout");
	private static final List<String> GET_EXCLUDED_PATHS = List.of("/posts", "/posts/.*", "/news", "/mentoring/\\d+/reviews", "/mentoring", "/mentoring.*", "/users/schedule");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws ServletException, IOException {

		String requestPath = request.getRequestURI();
		String method = request.getMethod();

		// 제외된 경로 처리
		if (isExcludedPath(requestPath, method)) {
			chain.doFilter(request, response);
			return;
		}

		// 쿠키에서 JWT 토큰 추출
		String token = getCookie(request, ACCESS_TOKEN);

		if (token == null) {
			chain.doFilter(request, response);
			return;
		}

		try {
			// 토큰 검증
			if (jwtProvider.validationAccessToken(token)) {
				String email = jwtProvider.extractEmail(token);
				String role = jwtProvider.extractRole(token);
				Long userId = jwtProvider.extractUserId(token);

				// 인증 객체 생성
				if (role != null) {
					// ROLE_ADMIN이면 어드민 권한 부여
					if (role.equals("ROLE_ADMIN")) {
						log.debug("Admin access granted for user: {}", email);
					} else {
						log.debug("User access granted for user: {}", email);
					}

					// 인증 객체 설정
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						email, null, Collections.singletonList(new SimpleGrantedAuthority(role)));

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);

					log.debug("Authentication set for user: {}", email);

					// 토큰 갱신 처리
					if (jwtProvider.validationRefreshToken(token)) {
						String newRefreshToken = jwtProvider.generateRefreshToken(userId);
						response.addCookie(jwtProvider.createHttpOnlyCookie("refresh_token", newRefreshToken, REFRESH_TOKEN_EXPIRATION));
					} else {
						log.warn("Invalid access token for user.");
					}
				}
			}
		} catch (Exception e) {
			log.error("Cannot set user authentication: {}", e.getMessage());
		}

		chain.doFilter(request, response);
	}

	/**
	 * 입력된 경로를 jwt 필터에서 제외
	 * @param path 경로
	 * @param method 요청 메서드
	 * @return 검증 여부
	 */
	private boolean isExcludedPath(String path, String method) {
		if ("GET".equalsIgnoreCase(method) && GET_EXCLUDED_PATHS.stream().anyMatch(path::matches)) {
			return true;
		}
		if ("POST".equalsIgnoreCase(method) && POST_EXCLUDED_PATHS.stream().anyMatch(path::matches)) {
			return true;
		}
		return false;
	}

	/**
	 * 쿠키에서 JWT 토큰을 가져오는 메서드
	 * @param request HTTP 요청
	 * @param cookieName 쿠키 이름
	 * @return 쿠키 값
	 */
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
