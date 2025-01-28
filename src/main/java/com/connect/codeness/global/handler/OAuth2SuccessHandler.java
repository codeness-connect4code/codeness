package com.connect.codeness.global.handler;

import static com.connect.codeness.global.constants.Constants.FRONTEND_URL;

import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.enums.UserProvider;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final OAuth2AuthorizedClientService authorizedClientService;

	public OAuth2SuccessHandler(JwtProvider jwtProvider, UserRepository userRepository,
		OAuth2AuthorizedClientService authorizedClientService) {
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
		this.authorizedClientService = authorizedClientService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		OAuth2AuthenticationToken oAuth2Authentication = (OAuth2AuthenticationToken) authentication;

		OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
			oAuth2Authentication.getAuthorizedClientRegistrationId(),
			oAuth2Authentication.getName());

		String accessToken = authorizedClient.getAccessToken().getTokenValue();

		try {
			String email = oAuth2User.getAttribute("email");
			String name = oAuth2User.getAttribute("name");

			User user = userRepository.findByEmail(email).orElseGet(() -> {
				User newUser = User.builder()
					.email(email)
					.name(name)
					.userNickname(name)
					.provider(UserProvider.GOOGLE)
					.role(UserRole.MENTEE)
					.googleToken(accessToken).build();
				return userRepository.save(newUser);
			});

			user.updateGoogleToken(accessToken);
			userRepository.save(user);

			// JWT 생성
			String jwtAccessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getId(),
				user.getRole().toString(), user.getProvider().toString());

			String jwtRefreshToken = jwtProvider.generateRefreshToken(user.getId());

			// 쿠키 설정 (HttpOnly & Secure)
			addHttpOnlyCookie(response, "access_token", jwtAccessToken, 60 * 60 * 24);
			addHttpOnlyCookie(response, "refresh_token", jwtRefreshToken, 60 * 60 * 24 * 7);

			// 리다이렉트 (토큰을 쿼리파라미터에 포함하지 않음)
			String redirectUrl = UriComponentsBuilder.fromUriString(
					System.getenv().getOrDefault("FRONTEND_URL", FRONTEND_URL)).build()
				.toUriString();

			response.sendRedirect(redirectUrl);

		} catch (Exception e) {
			log.error("OAuth2 로그인 처리 중 오류", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	// HttpOnly 쿠키 추가 메서드
	private void addHttpOnlyCookie(HttpServletResponse response, String name, String value,
		int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true); // HTTPS 환경에서만 전송 (운영 환경에서 적용)
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}
}
