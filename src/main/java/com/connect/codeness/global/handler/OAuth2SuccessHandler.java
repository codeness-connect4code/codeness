package com.connect.codeness.global.handler;

import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.jwt.JwtUtil;
import com.connect.codeness.global.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException {

		log.info("OAuth2 로그인 성공 핸들러 실행");
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

		log.info("OAuth2 사용자 정보: {}", oAuth2User.getAttributes());

		try {
			String email = oAuth2User.getAttribute("email");
			String name = oAuth2User.getAttribute("name");
			log.info("이메일: {}, 이름: {}", email, name);

			User user = userRepository.findByEmail(email)
				.orElseGet(() -> {
					log.info("새 사용자 생성");
					User newUser = User.builder()
						.email(email)
						.name(name)
						.provider("GOOGLE")
						.role(UserRole.MENTEE)
						.build();
					return userRepository.save(newUser);
				});

			log.info("사용자 정보: {}", user);

			String token = jwtUtil.generateToken(
				user.getEmail(),
				user.getId(),
				user.getRole().toString()
			);
			log.info("토큰 생성: {}", token);

			String redirectUrl = UriComponentsBuilder
				.fromUriString("/payment.html")
				.queryParam("token", token)
				.build()
				.toUriString();

			log.info("리다이렉트 URL: {}", redirectUrl);
			response.sendRedirect(redirectUrl);

		} catch (Exception e) {
			log.error("OAuth2 로그인 처리 중 오류", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}
