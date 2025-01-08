package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	//todo : 검증, 이메일중복예외처리
	@PostMapping
	public ResponseEntity<CommonResponseDto> createUser(@RequestBody UserCreateRequestDto userCreateRequestDto) {
		userService.createUser(userCreateRequestDto);
		CommonResponseDto response = CommonResponseDto.builder()
			.msg("회원가입 완료")
			.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
