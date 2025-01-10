package com.connect.codeness.global.Jwt;

import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailService userDetailService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain
	) throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");

		String username = null;
		String token = null;

		// JWT 토큰 추출
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			token = authorizationHeader.substring(7); // "Bearer " 이후의 토큰만 추출
			try {
				username = jwtUtil.extractEmail(token); // JWT에서 사용자 이메일 추출
			} catch (Exception e) {
				throw new BusinessException(ExceptionType.INVALID_TOKEN);
			}
		}

		// 사용자 이름이 있고 SecurityContext에 인증 정보가 없는 경우
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				// 데이터베이스에서 사용자 정보 로드
				UserDetails userDetails = userDetailService.loadUserByUsername(username);

				// JWT 토큰 유효성 검사
				if (jwtUtil.validateToken(token, userDetails.getUsername())) {

					// 사용자 권한 설정 (Role 접두어 'ROLE_' 추가)
					UsernamePasswordAuthenticationToken authToken =
						new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authToken);
				} else {
					throw new BusinessException(ExceptionType.INVALID_TOKEN);
				}
			} catch (Exception e) {
				throw new BusinessException(ExceptionType.INVALID_TOKEN);
			}
		}

		// 다음 필터로 요청 전달
		chain.doFilter(request, response);
	}
	private String extractRole(Collection<? extends GrantedAuthority> authorities) {
		return authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.findFirst()
			.orElse(null);
	}
}
