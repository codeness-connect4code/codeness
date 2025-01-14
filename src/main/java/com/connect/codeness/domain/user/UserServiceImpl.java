package com.connect.codeness.domain.user;

import com.connect.codeness.domain.file.FileRepository;
import com.connect.codeness.domain.file.FileService;
import com.connect.codeness.domain.file.FileServiceImpl;
import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserDeleteResponseDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.awt.Image;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final FileRepository fileRepository;
	private final FileService fileService;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
		AuthenticationManager authenticationManager, JwtUtil jwtUtil, FileRepository fileRepository,
		FileServiceImpl fileService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.fileRepository = fileRepository;
		this.fileService = fileService;
	}


	@Override
	@Transactional
	public CommonResponseDto createUser(UserCreateRequestDto dto) {
		String encodedPassword = passwordEncoder.encode(dto.getPassword());

		if(userRepository.existsByEmail(dto.getEmail())){
			throw new BusinessException(ExceptionType.ALREADY_EXIST_EMAIL);
		}

		User user = new User().builder()
			.email(dto.getEmail())
			.password(encodedPassword)
			.name(dto.getName())
			.userNickname(dto.getNickname())
			.phoneNumber(dto.getPhoneNumber())
			.field(dto.getField())
			.role(dto.getUserRole())
			.build();

		userRepository.save(user);

		return CommonResponseDto.builder().msg("회원가입 완료").build();
	}

	@Override
	public String login(LoginRequestDto dto) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
		);

		User user = userRepository.findByEmailOrElseThrow(dto.getEmail());

		String token = jwtUtil.generateToken(user.getEmail(),user.getId(),user.getRole().toString());

		return token;
	}

	@Override
	public CommonResponseDto getUser(Long userId) {
		User user = userRepository.findByIdOrElseThrow(userId);

		UserResponseDto userResponseDto = UserResponseDto.builder()
			.name(user.getName())
			.nickname(user.getUserNickname())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.region(user.getRegion())
			.field(user.getField())
			.career(user.getCareer())
			.mbti(user.getMbti())
			.siteLink(user.getSite_link()).build();

		return CommonResponseDto.builder()
			.msg("마이프로필 조회 성공")
			.data(userResponseDto).build();
	}

	@Override
	@Transactional
	public CommonResponseDto updateUser(Long userId, UserUpdateRequestDto dto, ImageFile imageFile)
		throws IOException {
		User user = userRepository.findByIdOrElseThrow(userId);
		user.update(dto, imageFile);
		userRepository.save(user);
		return CommonResponseDto.builder().msg("유저 수정 완료").build();
	}


	@Override
	@Transactional
	public CommonResponseDto updatePassword(Long userId,
		UserPasswordUpdateRequestDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		if(!passwordEncoder.matches(dto.getCurrentPassword(),user.getPassword())){
			throw new BusinessException(ExceptionType.UNAUTHORIZED_PASSWORD);
		}
		user.setPassword(dto.getNewPassword());
		userRepository.save(user);
		return CommonResponseDto.builder().msg("패스워드 수정 완료").build();
	}

	@Override
	@Transactional
	public CommonResponseDto updateBankAccount(Long userId,
		UserBankUpdateRequestDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		user.setBank(dto.getBankName(),dto.getBankAccount());
		userRepository.save(user);
		return CommonResponseDto.builder().msg("계좌 입력 완료").build();
	}

	@Override
	@Transactional
	public CommonResponseDto deleteUser(Long userId, UserDeleteResponseDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		if (!passwordEncoder.matches(dto.getPassword(),user.getPassword())){
			throw new BusinessException(ExceptionType.UNAUTHORIZED_PASSWORD);
		}
		user.deleteUser();
		userRepository.save(user);
		return CommonResponseDto.builder().msg("회원 탈퇴 완료").build();
	}
}
