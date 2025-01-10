package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
		AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}


	@Override
	@Transactional
	public CommonResponseDto createUser(UserCreateRequestDto dto) {
		String encodedPassword = passwordEncoder.encode(dto.getPassword());

		if(userRepository.existsByEmail(dto.getEmail())){
			throw new BusinessException(ExceptionType.ALEADY_EXIST_EMAIL);
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
}

