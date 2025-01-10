package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.JwtResponseDto;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
	private final UserService userService;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	public UserController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.jwtUtil =  jwtUtil;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/signup")
	public ResponseEntity<CommonResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
		CommonResponseDto response = userService.createUser(userCreateRequestDto);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	//todo : 프론트 구현시 토큰 출력 삭제
	@PostMapping("/login")
	public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
		JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
			.token(userService.login(loginRequestDto))
			.dto(CommonResponseDto.builder()
				.msg("로그인 성공")
				.build())
			.build();
		return new ResponseEntity<>(jwtResponseDto, HttpStatus.OK);
	}
}
