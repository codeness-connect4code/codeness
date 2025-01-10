package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface UserService {
	CommonResponseDto createUser(UserCreateRequestDto userCreateRequestDto);
	String login(LoginRequestDto loginRequestDto);
	CommonResponseDto getUser(String email);
}

