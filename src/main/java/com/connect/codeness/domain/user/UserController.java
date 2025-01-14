package com.connect.codeness.domain.user;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.file.FileRepository;
import com.connect.codeness.domain.file.FileService;
import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.file.dto.FileCreateDto;
import com.connect.codeness.domain.user.dto.JwtResponseDto;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserDeleteResponseDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.constants.Constants;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import jakarta.validation.Valid;
import java.io.IOException;
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
	public ResponseEntity<CommonResponseDto> getUser(@RequestHeader(AUTHORIZATION)String authorizationHeader) {
		String token = authorizationHeader.substring("Bearer ".length());
		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto commonResponseDto = userService.getUser(userId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 *
	 * @param authorizationHeader
	 * @param userUpdateRequestDto
	 * @param userId
	 * @return
	 */
	@PatchMapping("/users/{userId}")
	public ResponseEntity<CommonResponseDto> updateUser(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@ModelAttribute UserUpdateRequestDto userUpdateRequestDto,
		@PathVariable Long userId
	)throws IOException{
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);
		if (userId != tokenId){
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}

		if (
			fileRepository.findByUserIdAndFileCategory(userId, FileCategory.PROFILE).isPresent()
		){
			fileService.deleteFile(userId,FileCategory.PROFILE);
		}

		CommonResponseDto fileDto =
			fileService.createFile(
				userUpdateRequestDto.getMultipartFile(), userId, FileCategory.PROFILE);
		ImageFile imageFile = fileRepository.findByUserIdAndFileCategoryOrElseThrow(userId, FileCategory.PROFILE);
		CommonResponseDto dto = userService.updateUser(userId, userUpdateRequestDto, imageFile);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}


	@PatchMapping("/users/{userId}/password")
	public ResponseEntity<CommonResponseDto> updatePassword(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
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
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
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

	@DeleteMapping("/users/{userId}")
	public ResponseEntity<CommonResponseDto> deleteUser(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestBody UserDeleteResponseDto userDeleteResponseDto,
		@PathVariable Long userId
	){
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);
		if(userId != tokenId){
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}
		CommonResponseDto commonResponseDto = userService.deleteUser(userId,userDeleteResponseDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

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
