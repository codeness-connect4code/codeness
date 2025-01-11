package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.JwtResponseDto;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

	/**
	 *
	 * @param userCreateRequestDto
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<CommonResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
		CommonResponseDto response = userService.createUser(userCreateRequestDto);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	//todo : 프론트 구현시 토큰 출력 삭제

	/**
	 *
	 * @param loginRequestDto
	 * @return
	 */
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

	/**
	 *
	 * @param authorizationHeader
	 * @return
	 */
	@GetMapping("/users")
	public ResponseEntity<CommonResponseDto> getUser(@RequestHeader("Authorization")String authorizationHeader) {
		String token = authorizationHeader.substring("Bearer ".length());
		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto commonResponseDto = userService.getUser(userId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	//todo : 이미지 업로드 추가
	/**
	 *
	 * @param authorizationHeader
	 * @param userUpdateRequestDto
	 * @param userId
	 * @return
	 */
	@PatchMapping("/users/{userId}")
	public ResponseEntity<CommonResponseDto> updateUser(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody UserUpdateRequestDto userUpdateRequestDto,
		@PathVariable Long userId
	){
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);
		if(userId != tokenId){
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}
		CommonResponseDto commonResponseDto = userService.updateUser(userId, userUpdateRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	@PatchMapping("/users/{userId}/password")
	public ResponseEntity<CommonResponseDto> updatePassword(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody UserPasswordUpdateRequestDto userPasswordUpdateRequestDto,
		@PathVariable Long userId
	){
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);
		if(userId != tokenId){
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}
		CommonResponseDto commonResponseDto = userService.updatePassword(userId,
			userPasswordUpdateRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	@PatchMapping("/users/{userId}/bank-account")
	public ResponseEntity<CommonResponseDto> updateBankAccount(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestBody UserBankUpdateRequestDto userBankUpdateRequestDto,
		@PathVariable Long userId
	){
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);
		if(userId != tokenId){
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}
		CommonResponseDto commonResponseDto = userService.updateBankAccount(userId,userBankUpdateRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}
}
