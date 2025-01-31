package com.connect.codeness.domain.user.controller;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.file.repository.FileRepository;
import com.connect.codeness.domain.file.service.FileService;
import com.connect.codeness.domain.user.dto.GoogleUserUpdateRequestDto;
import com.connect.codeness.domain.user.dto.JwtResponseDto;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserDeleteResponseDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.domain.user.service.UserService;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class UserController {

	private final UserService userService;
	private final JwtProvider jwtProvider;
	private final FileService fileService;
	private final FileRepository fileRepository;

	public UserController(UserService userService, JwtProvider jwtProvider, FileService fileService,
		FileRepository fileRepository) {
		this.userService = userService;
		this.jwtProvider = jwtProvider;
		this.fileService = fileService;
		this.fileRepository = fileRepository;
	}

	/**
	 * 회원가입 API
	 *
	 * @param userCreateRequestDto 회원가입 요청 dto
	 * @return 성공 메세지
	 */
	@PostMapping("/signup")
	public ResponseEntity<CommonResponseDto<?>> createUser(
		@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
		CommonResponseDto<?> response = userService.createUser(userCreateRequestDto);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	//todo : 프론트 구현시 토큰 출력 삭제

	/**
	 * 로그인 API
	 *
	 * @param loginRequestDto 로그인 요청 dto
	 * @param response        HTTP-Only 쿠키 저장을 위한 response
	 * @return 액세스 토큰(헤더), 리프레시 토큰(HTTP-Only)
	 * @throws IOException
	 */
	@PostMapping("/login")
	public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequestDto,
		HttpServletResponse response) throws IOException {
		JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
			.token(userService.login(loginRequestDto, response))
			.dto(CommonResponseDto.builder().msg("로그인 성공").build()).build();
		return new ResponseEntity<>(jwtResponseDto, HttpStatus.OK);
	}

	/**
	 * 유저 상세 조회 API
	 *
	 * @param authorizationHeader 액세스 토큰 헤더
	 * @return 성공 메세지, 유저 상세 조회 데이터
	 */
	@GetMapping("/users")
	public ResponseEntity<CommonResponseDto<?>> getUser(
		@RequestHeader(AUTHORIZATION) String authorizationHeader) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);
		CommonResponseDto<?> commonResponseDto = userService.getUser(userId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 유저 정보 수정 API (LOCAL 회원가입)
	 *
	 * @param authorizationHeader  액세스 토큰 헤더
	 * @param userUpdateRequestDto 유저 정보 수정 dto
	 * @return 성공 메세지
	 * @throws IOException
	 */
	@PatchMapping("/users")
	public ResponseEntity<CommonResponseDto<?>> updateUser(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@ModelAttribute UserUpdateRequestDto userUpdateRequestDto) throws IOException {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		if (fileRepository.findByUserIdAndFileCategory(userId, FileCategory.PROFILE).isPresent()) {
			fileService.deleteFile(userId, FileCategory.PROFILE);
		}

		CommonResponseDto<?> fileDto = fileService.createFile(
			userUpdateRequestDto.getMultipartFile(), userId, FileCategory.PROFILE);
		ImageFile imageFile = fileRepository.findByUserIdAndFileCategoryOrElseThrow(userId,
			FileCategory.PROFILE);
		CommonResponseDto<?> dto = userService.updateUser(userId, userUpdateRequestDto, imageFile);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	/**
	 * 유저 정보 수정 API (GOOGLE 회원가입)
	 *
	 * @param authorizationHeader        액세스 토큰 헤더
	 * @param googleUserUpdateRequestDto 구글 유저 정보 수정 dto
	 * @return 성공 메세지
	 * @throws IOException
	 */
	@PatchMapping("/google/users")
	public ResponseEntity<CommonResponseDto<?>> updateGoogleUser(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@ModelAttribute GoogleUserUpdateRequestDto googleUserUpdateRequestDto) throws IOException {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		if (fileRepository.findByUserIdAndFileCategory(userId, FileCategory.PROFILE).isPresent()) {
			fileService.deleteFile(userId, FileCategory.PROFILE);
		}

		CommonResponseDto<?> fileDto = fileService.createFile(
			googleUserUpdateRequestDto.getMultipartFile(), userId, FileCategory.PROFILE);
		ImageFile imageFile = fileRepository.findByUserIdAndFileCategoryOrElseThrow(userId,
			FileCategory.PROFILE);
		CommonResponseDto<?> dto = userService.updateGoogleUser(userId, googleUserUpdateRequestDto,
			imageFile);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	/**
	 * 유저 비밀번호 수정 API
	 *
	 * @param authorizationHeader          액세스 토큰 헤더
	 * @param userPasswordUpdateRequestDto 비밀번호 수정 dto
	 * @return 성공 메세지
	 */
	@PatchMapping("/users/password")
	public ResponseEntity<CommonResponseDto<?>> updatePassword(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestBody UserPasswordUpdateRequestDto userPasswordUpdateRequestDto) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<?> commonResponseDto = userService.updatePassword(userId,
			userPasswordUpdateRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 유저 계좌번호 수정 API
	 *
	 * @param authorizationHeader      액세스 토큰 헤더
	 * @param userBankUpdateRequestDto 유저 계좌 업데이트 dto
	 * @return 성공 메세지
	 */
	@PatchMapping("/users/bank-account")
	public ResponseEntity<CommonResponseDto<?>> updateBankAccount(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestBody UserBankUpdateRequestDto userBankUpdateRequestDto) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<?> commonResponseDto = userService.updateBankAccount(userId,
			userBankUpdateRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 회원 탈퇴 API
	 *
	 * @param authorizationHeader   액세스 토큰 헤더
	 * @param userDeleteResponseDto 회원 탈퇴 dto
	 * @return 성공 메세지
	 */
	@DeleteMapping("/users")
	public ResponseEntity<CommonResponseDto<?>> deleteUser(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestBody UserDeleteResponseDto userDeleteResponseDto) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<?> commonResponseDto = userService.deleteUser(userId,
			userDeleteResponseDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 유저 멘토링 공고 추천 API
	 *
	 * @param authorizationHeader 액세스 토큰 헤더
	 * @return 멘토링 공고 리스트
	 */
	@GetMapping("/users/mentoring")
	public ResponseEntity<CommonResponseDto<?>> getRecommendMentor(
		@RequestHeader(AUTHORIZATION) String authorizationHeader) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<?> commonResponseDto = userService.getMentoring(userId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}
}