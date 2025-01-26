package com.connect.codeness.global.handler;

import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.jwt.JwtUtil;
import com.connect.codeness.global.enums.UserRole;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
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
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final OAuth2AuthorizedClientService authorizedClientService;

	public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository, OAuth2AuthorizedClientService authorizedClientService) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
		this.authorizedClientService = authorizedClientService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException {

		//OAuth2 인증 성공시 사용자 정보를 가져옴
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		OAuth2AuthenticationToken oAuth2Authentication = (OAuth2AuthenticationToken) authentication;

		//인증된 클라이언트 정보를 가져옴
		OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
			oAuth2Authentication.getAuthorizedClientRegistrationId(),
			oAuth2Authentication.getName()
		);

		//OAuth2 액세스 토큰 가져옴
		String accessToken = authorizedClient.getAccessToken().getTokenValue();

		try {
			//OAuth2에서 가져온 정보를 이메일과 이름으로 저장
			String email = oAuth2User.getAttribute("email");
			String name = oAuth2User.getAttribute("name");

			//위의 정보 바탕으로 user 객체 생성
			User user = userRepository.findByEmail(email)
				.orElseGet(() -> {
					User newUser = User.builder()
						.email(email)
						.name(name)
						.provider("GOOGLE")    //provider - 구글 로그인 의미
						.role(UserRole.MENTEE)
						.googleToken(accessToken)  //OAuth2 액세스 토큰도 함께 저장
						.build();
					return userRepository.save(newUser);
				});

			//기존 사용자일경우 토큰 갱신
			user.updateGoogleToken(accessToken);
			userRepository.save(user);

			//로그인 jwt 토큰 생성
			String token = jwtUtil.generateToken(
				user.getEmail(),
				user.getId(),
				user.getRole().toString(),
				user.getProvider()
			);

			//일련의 과정 완료시 리다이렉트
			String redirectUrl = UriComponentsBuilder
				.fromUriString(System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:3000") + "/payment")
				.queryParam("token", token)
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