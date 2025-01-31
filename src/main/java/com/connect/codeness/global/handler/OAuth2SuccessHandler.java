package com.connect.codeness.global.handler;

import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN;
import static com.connect.codeness.global.constants.Constants.FRONTEND_URL;
import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN;
import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN_EXPIRATION;

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

			// 리프레시 토큰을 HttpOnly 쿠키에 저장
			jwtProvider.createHttpOnlyCookie(REFRESH_TOKEN, jwtRefreshToken, REFRESH_TOKEN_EXPIRATION);

			// 프론트엔드로 리다이렉트하면서 액세스 토큰을 쿼리 파라미터로 전달
			String redirectUrl = UriComponentsBuilder.fromUriString(
					System.getenv().getOrDefault("FRONTEND_URL", FRONTEND_URL))
				.queryParam(ACCESS_TOKEN, jwtAccessToken)
				.build()
				.toUriString();

			response.sendRedirect(redirectUrl);

		} catch (Exception e) {
			log.error("OAuth2 로그인 처리 중 오류", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}
