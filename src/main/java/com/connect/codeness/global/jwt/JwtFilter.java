package com.connect.codeness.global.jwt;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;
import static com.connect.codeness.global.constants.Constants.BEARER;
import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN;
import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN_EXPIRATION;

import com.connect.codeness.domain.user.dto.UserLoginDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.service.RedisLoginService;
import io.jsonwebtoken.ExpiredJwtException;
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

	private final UserRepository userRepository;
	private static final List<String> POST_EXCLUDED_PATHS = List.of("/login", "/signup", "/logout");
	private static final List<String> GET_EXCLUDED_PATHS = List.of("/posts", "/posts/.*", "/news",
		"/mentoring/\\d+/reviews", "/mentoring", "/mentoring.*", "/users/schedule");
	private final JwtProvider jwtProvider;
	private final RedisLoginService redisLoginService;

	public JwtFilter(UserRepository userRepository, JwtProvider jwtProvider,
		RedisLoginService redisLoginService) {
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
		this.redisLoginService = redisLoginService;
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
				//redis 중복 로그인 검증
				if (!redisLoginService.validateToken(jwtProvider.extractUserId(accessToken), accessToken)){
					log.warn("redis 에서 토큰 검증이 되지 않음, 중복 로그인 감지");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().write("{\"message\":\"다른 기기에서 로그인이 감지되어 로그아웃됩니다.\"}");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
				// Access Token이 유효한 경우, 인증 정보 설정
				if (jwtProvider.validationAccessToken(accessToken)) {

					setAuthentication(accessToken, request);

				}

		}
		catch (ExpiredJwtException e) {
			log.error("Expired access token!");

			// refreshToken이 없는 경우 401 응답
			if (refreshToken == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json;charset=UTF-8");

				try {
					String jsonResponse = "{\"message\":\"Access token has expired\",\"status\":401}";
					response.getWriter().write(jsonResponse);
				} catch (IOException ex) {
					log.error("Failed to write error response when generating new access_token.", ex);
				}
				return;
			}

			log.error("regenerating access token!");
			//새 액세스 토큰 발급 로직
			String userId = jwtProvider.extractUserIdFromRefresh(refreshToken);

			User user = userRepository.findByIdOrElseThrow(Long.valueOf(userId));

			// 새 Access Token 발급
			String newAccessToken = jwtProvider.regenerateAccessToken(refreshToken, user.getEmail(),
				user.getRole().toString(), user.getProvider().toString());

			log.info("newAccessToken: {}", newAccessToken);

			// 응답 헤더에 새 Access Token 설정
			response.setHeader(AUTHORIZATION, BEARER + newAccessToken);

			//redis 중복 로그인 검증
			if (!redisLoginService.validateToken(jwtProvider.extractUserId(accessToken), accessToken)){
				log.warn("redis 에서 토큰 검증이 되지 않음, 중복 로그인 감지");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write("{\"message\":\"다른 기기에서 로그인이 감지되어 로그아웃됩니다.\"}");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

					// 응답 헤더에 새 Access Token 설정
					response.setHeader(AUTHORIZATION, BEARER + newAccessToken);

			// 새 Access Token을 사용하여 인증 정보 설정
			setAuthentication(newAccessToken, request);

			// 새로운 Refresh Token 발급 및 설정
			String newRefreshToken = jwtProvider.generateRefreshToken(Long.valueOf(userId));
			response.addCookie(
				jwtProvider.createHttpOnlyCookie(REFRESH_TOKEN, newRefreshToken,
					REFRESH_TOKEN_EXPIRATION));

			UserLoginDto newLoginDto = UserLoginDto.builder()
				.id(user.getId())
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken)
				.build();

			redisLoginService.saveLoginInfo(newLoginDto);

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");

			try {
				String jsonResponse = "{\"message\":\"Access token has expired\",\"status\":401}";
				response.getWriter().write(jsonResponse);
			} catch (IOException ex) {
				log.error("Failed to write error response", ex);
			}
			return;
		}
		catch (Exception e) {
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
