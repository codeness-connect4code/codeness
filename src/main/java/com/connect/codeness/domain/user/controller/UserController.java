package com.connect.codeness.domain.user.controller;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.file.repository.FileRepository;
import com.connect.codeness.domain.file.service.FileService;
import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.domain.user.dto.JwtResponseDto;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserDeleteResponseDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.domain.user.service.UserService;
import com.connect.codeness.global.jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.http.GET;

@RestController
@RequestMapping
@Slf4j
public class UserController {
	private final UserService userService;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final FileService fileService;
	private final FileRepository fileRepository;
	private final UserRepository userRepository;

	public UserController(
		UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager
		, FileService fileService,
		FileRepository fileRepository, UserRepository userRepository) {
		this.userService = userService;
		this.jwtUtil =  jwtUtil;
		this.authenticationManager = authenticationManager;
		this.fileService = fileService;
		this.fileRepository = fileRepository;
		this.userRepository = userRepository;
	}

	/**
	 * 회원가입 API
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
	 * 로그인 API
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
	 * 유저 상세 조회 API
	 * @param authorizationHeader
	 * @return
	 */
	@GetMapping("/users")
	public ResponseEntity<CommonResponseDto> getUser(@RequestHeader(AUTHORIZATION)String authorizationHeader) {
		String token = authorizationHeader.substring("Bearer ".length());
		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto commonResponseDto = userService.getUser(userId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 유저 정보 수정 API
	 * @param authorizationHeader
	 * @param userUpdateRequestDto
	 * @return
	 */
	@PatchMapping("/users")
	public ResponseEntity<CommonResponseDto> updateUser(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@ModelAttribute UserUpdateRequestDto userUpdateRequestDto
	)throws IOException{
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);

		if (
			fileRepository.findByUserIdAndFileCategory(tokenId, FileCategory.PROFILE).isPresent()
		){
			fileService.deleteFile(tokenId,FileCategory.PROFILE);
		}

		CommonResponseDto fileDto =
			fileService.createFile(
				userUpdateRequestDto.getMultipartFile(), tokenId, FileCategory.PROFILE);
		ImageFile imageFile = fileRepository.findByUserIdAndFileCategoryOrElseThrow(tokenId, FileCategory.PROFILE);
		CommonResponseDto dto = userService.updateUser(tokenId, userUpdateRequestDto, imageFile);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	/**
	 * 유저 비밀번호 수정 API
	 * @param authorizationHeader
	 * @param userPasswordUpdateRequestDto
	 * @return
	 */
	@PatchMapping("/users/password")
	public ResponseEntity<CommonResponseDto> updatePassword(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestBody UserPasswordUpdateRequestDto userPasswordUpdateRequestDto
	){
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);

		CommonResponseDto commonResponseDto = userService.updatePassword(tokenId,
			userPasswordUpdateRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 유저 계좌번호 수정 API
	 * @param authorizationHeader
	 * @param userBankUpdateRequestDto
	 * @return
	 */
	@PatchMapping("/users/bank-account")
	public ResponseEntity<CommonResponseDto> updateBankAccount(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestBody UserBankUpdateRequestDto userBankUpdateRequestDto
	){
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);

		CommonResponseDto commonResponseDto = userService.updateBankAccount(tokenId,userBankUpdateRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 유저 탈퇴 API
	 * @param authorizationHeader
	 * @param userDeleteResponseDto
	 * @return
	 */
	@DeleteMapping("/users")
	public ResponseEntity<CommonResponseDto> deleteUser(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestBody UserDeleteResponseDto userDeleteResponseDto
	){
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);

		CommonResponseDto commonResponseDto = userService.deleteUser(tokenId,userDeleteResponseDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 유저 추천 멘토링 공고 조회 API
	 * @param authorizationHeader
	 * @return
	 */
	 @GetMapping("/users/mentoring")
	public ResponseEntity<CommonResponseDto> getRecommendMentor(
		@RequestHeader(AUTHORIZATION) String authorizationHeader
	 ){
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);

		CommonResponseDto commonResponseDto = userService.getMentoring(tokenId);
		return new ResponseEntity<>(commonResponseDto,HttpStatus.OK);
	 }
}
