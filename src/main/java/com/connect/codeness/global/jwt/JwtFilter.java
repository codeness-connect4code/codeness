package com.connect.codeness.global.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

	@Autowired
	private JwtUtil jwtUtil;

	// 제외할 경로
	private static final List<String> POST_EXCLUDED_PATHS = List.of("/login", "/signup", "/logout");
	private static final List<String> GET_EXCLUDED_PATHS = List.of("/posts", "/posts/.*", "/news", "/mentoring/\\d+/reviews");
	private static final List<String> EXCLUDED_PATHS = List.of("/payment", "/mentoring", "/mentoring/.*", "/login-page", "/users", "/loginPage.html", "/payment.html");

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

		// Authorization 헤더에서 JWT 토큰 추출
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		String token = authorizationHeader.substring(7);

		try {
			// 토큰 검증
			if (jwtUtil.validateToken(token)) {
				String email = jwtUtil.extractEmail(token);
				String role = jwtUtil.extractRole(token);
				Long userId = jwtUtil.extractUserId(token);

				// 인증 객체 생성
				if (role != null) {
					// ROLE_ADMIN이면 어드민 권한 부여
					if (role.equals("ROLE_ADMIN")) {
						logger.debug("Admin access granted for user: {}", email);
					} else {
						logger.debug("User access granted for user: {}", email);
					}

					// 인증 객체 설정
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						email, null, Collections.singletonList(new SimpleGrantedAuthority(role)));

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);

					logger.debug("Authentication set for user: {}", email);

					// 토큰 갱신 처리
					if (jwtUtil.needsRefresh(token)) {
						String newToken = jwtUtil.refreshToken(token);
						response.setHeader("New-Token", newToken);
						logger.debug("Token refreshed for user: {}", email);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e.getMessage());
		}

		chain.doFilter(request, response);
	}

	// 제외된 경로 처리
	private boolean isExcludedPath(String path, String method) {
		if (EXCLUDED_PATHS.stream().anyMatch(path::matches)) {
			return true;
		}
		if ("GET".equalsIgnoreCase(method) && GET_EXCLUDED_PATHS.stream().anyMatch(path::matches)) {
			return true;
		}
		if ("POST".equalsIgnoreCase(method) && POST_EXCLUDED_PATHS.stream().anyMatch(path::matches)) {
			return true;
		}
		return false;
	}
}
