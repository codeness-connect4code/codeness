package com.connect.codeness.global.jwt;

import com.connect.codeness.global.security.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailService userDetailService;

	private static final List<String> POST_EXCLUDED_PATHS = List.of("/login", "/signup");
	private static final List<String> GET_EXCLUDED_PATHS = List.of("/posts", "/posts/.*","/news");
	private static final List<String> DELETE_EXCLUDED_PATHS = List.of("");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws ServletException, IOException {

		String requestPath = request.getRequestURI();
		String method = request.getMethod();

		// GET 메서드에 대한 화이트리스트 확인
		if ("GET".equalsIgnoreCase(method) && GET_EXCLUDED_PATHS.stream().anyMatch(requestPath::matches)) {
			chain.doFilter(request, response);
			return;
		}

		// POST 메서드에 대한 화이트리스트 확인
		if ("POST".equalsIgnoreCase(method) && POST_EXCLUDED_PATHS.stream().anyMatch(requestPath::matches)) {
			chain.doFilter(request, response);
			return;
		}

		// DELETE 메서드에 대한 화이트리스트 확인
//		if ("DELETE".equalsIgnoreCase(method) && DELETE_EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith)) {
//			chain.doFilter(request, response);
//			return;
//		}

		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			// 인증 헤더가 없거나 잘못된 경우
			logger.warn("Authorization header is missing or invalid.");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Unauthorized: Missing or invalid Authorization header");
			return;
		}

		String token = authorizationHeader.substring(7);
		String username = null;

		try {
			username = jwtUtil.extractEmail(token);  // JWT에서 이메일 추출
			logger.debug("Extracted email from JWT: " + username);
		} catch (Exception e) {
			// JWT 이메일 추출 오류 처리
			logger.error("Error extracting email from JWT", e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Bad Request: Invalid token");
			return;
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				UserDetails userDetails = userDetailService.loadUserByUsername(username);

				if (jwtUtil.validateToken(token, userDetails.getUsername())) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authToken);
					logger.debug("Authentication set for user: " + userDetails.getUsername());
				} else {
					// 유효하지 않은 토큰 처리
					logger.warn("Invalid JWT token for user: " + userDetails.getUsername());
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.getWriter().write("Unauthorized: Invalid token");
					return;
				}
			} catch (Exception e) {
				// 인증 처리 중 오류가 발생한 경우
				logger.error("Error during authentication", e);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Unauthorized: Authentication failed");
				return;
			}
		}

		chain.doFilter(request, response);
	}
}

