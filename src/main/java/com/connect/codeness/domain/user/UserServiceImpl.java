package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.global.config.PasswordEncoder;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}


	@Override
	@Transactional
	public void createUser(UserCreateRequestDto dto) {
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
	}
}

