package com.connect.codeness.domain.user.service;

import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN;
import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN_EXPIRATION;
import static com.connect.codeness.global.enums.UserProvider.GOOGLE;

import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostRecommendResponseDto;
import com.connect.codeness.domain.mentoringpost.repository.MentoringPostRepository;
import com.connect.codeness.domain.user.dto.GoogleUserUpdateRequestDto;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserDeleteResponseDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.UserProvider;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import com.connect.codeness.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
	private final JwtProvider jwtProvider;
	private final MentoringPostRepository mentoringPostRepository;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
		AuthenticationManager authenticationManager, JwtProvider jwtProvider,
		MentoringPostRepository mentoringPostRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtProvider = jwtProvider;
		this.mentoringPostRepository = mentoringPostRepository;
	}

	/**
	 * 유저 회원가입
	 *
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional
	public CommonResponseDto<?> createUser(UserCreateRequestDto dto) {
		//비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(dto.getPassword());

		//이메일 중복여부 확인
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new BusinessException(ExceptionType.ALREADY_EXIST_EMAIL);
		}

		//user 객체 생성 후 DB에 저장
		new User();
		User user = User.builder()
			.email(dto.getEmail())
			.password(encodedPassword)
			.name(dto.getName())
			.userNickname(dto.getNickname())
			.phoneNumber(dto.getPhoneNumber())
			.field(dto.getField())
			.role(dto.getUserRole())
			.provider(UserProvider.LOCAL).build();

		userRepository.save(user);

		return CommonResponseDto.builder().msg("회원가입 완료").build();
	}

	/**
	 * 로그인
	 *
	 * @param dto
	 * @return
	 */
	@Override
	public String login(LoginRequestDto dto, HttpServletResponse response) throws IOException {
		// 이메일, 비밀번호 검증
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

		User user = userRepository.findByEmailOrElseThrow(dto.getEmail());

		// 구글 회원가입 유저일 시 예외 반환
		if (user.getProvider().equals(GOOGLE)) {
			throw new BusinessException(ExceptionType.GOOGLE_PROVIDER);
		}

		// 로그인 성공 시 토큰 생성 후 반환
		String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getId(),
			user.getRole().toString(), UserProvider.LOCAL.toString());
		String refreshToken = jwtProvider.generateRefreshToken(user.getId());

		// 리프레시 토큰을 HTTP-Only 쿠키에 저장
		response.addCookie(jwtProvider.createHttpOnlyCookie(REFRESH_TOKEN, refreshToken,
			REFRESH_TOKEN_EXPIRATION));

		// 액세스 토큰은 응답 바디로 반환하여 클라이언트가 상태 관리
		return accessToken;
	}


	/**
	 * 유저 정보 조회
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public CommonResponseDto<?> getUser(Long userId) {
		User user = userRepository.findByIdOrElseThrow(userId);

		UserResponseDto userResponseDto = UserResponseDto.builder().name(user.getName())
			.userNickname(user.getUserNickname()).email(user.getEmail())
			.phoneNumber(user.getPhoneNumber()).region(user.getRegion()).field(user.getField())
			.career(user.getCareer()).mbti(user.getMbti()).siteLink(user.getSiteLink()).build();

		return CommonResponseDto.builder().msg("마이프로필 조회 성공").data(userResponseDto).build();
	}

	/**
	 * 유저 정보 수정
	 *
	 * @param userId
	 * @param dto
	 * @param imageFile
	 * @return
	 * @throws IOException
	 */
	@Override
	@Transactional
	public CommonResponseDto<?> updateUser(Long userId, UserUpdateRequestDto dto,
		ImageFile imageFile) throws IOException {
		User user = userRepository.findByIdOrElseThrow(userId);
		user.update(dto, imageFile);
		userRepository.save(user);
		return CommonResponseDto.builder().msg("유저 수정 완료").build();
	}

	/**
	 * 구글 유저 정보 수정
	 *
	 * @param userId
	 * @param dto
	 * @param imageFile
	 * @return
	 * @throws IOException
	 */
	@Override
	@Transactional
	public CommonResponseDto<?> updateGoogleUser(Long userId, GoogleUserUpdateRequestDto dto,
		ImageFile imageFile) throws IOException {
		User user = userRepository.findByIdOrElseThrow(userId);

		if (!user.getProvider().equals(UserProvider.GOOGLE)) {
			throw new BusinessException(ExceptionType.BAD_REQUEST);
		}

		user.update(dto, imageFile);
		userRepository.save(user);
		return CommonResponseDto.builder().msg("유저 수정 완료").build();
	}

	/**
	 * 유저 비밀번호 변경
	 *
	 * @param userId
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional
	public CommonResponseDto<?> updatePassword(Long userId, UserPasswordUpdateRequestDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);

		//구글 로그인시 비밀번호 변경 x
		if (user.getProvider().equals(UserProvider.GOOGLE)) {
			throw new BusinessException(ExceptionType.GOOGLE_PROVIDER);
		}

		//패스워드 확인
		if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
			throw new BusinessException(ExceptionType.UNAUTHORIZED_PASSWORD);
		}
		//새로 입력한 패스워드 암호화
		String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
		user.updatePassword(encodedPassword);
		userRepository.save(user);
		return CommonResponseDto.builder().msg("패스워드 수정 완료").build();
	}

	/**
	 * 유저 계좌번호 변경
	 *
	 * @param userId
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional
	public CommonResponseDto<?> updateBankAccount(Long userId, UserBankUpdateRequestDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		user.updateBank(dto.getBankName(), dto.getBankAccount());
		userRepository.save(user);
		return CommonResponseDto.builder().msg("계좌 입력 완료").build();
	}

	/**
	 * 유저 탈퇴(soft delete)
	 *
	 * @param userId
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional
	public CommonResponseDto<?> deleteUser(Long userId, UserDeleteResponseDto dto) {
		User user = userRepository.findByIdOrElseThrow(userId);
		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new BusinessException(ExceptionType.UNAUTHORIZED_PASSWORD);
		}
		user.deleteUser();
		userRepository.save(user);
		return CommonResponseDto.builder().msg("회원 탈퇴 완료").build();
	}

	/**
	 * 유저 멘토링 공고 추천
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public CommonResponseDto<?> getMentoring(Long userId) {
		User user = userRepository.findByIdOrElseThrow(userId);

		List<MentoringPostRecommendResponseDto> commendMentoringPost = mentoringPostRepository.findByFilter(
			user.getField(), user.getRegion());

		Collections.shuffle(commendMentoringPost);
		List<MentoringPostRecommendResponseDto> randList = commendMentoringPost.subList(0,
			Math.min(3, commendMentoringPost.size()));

		return CommonResponseDto.builder().msg("멘토링 공고 추천에 성공했습니다.").data(randList).build();
	}
}
