package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface UserService {
	CommonResponseDto createUser(UserCreateRequestDto userCreateRequestDto);
	String login(LoginRequestDto loginRequestDto);
	CommonResponseDto getUser(Long userId);
	CommonResponseDto updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto);
	CommonResponseDto updatePassword(Long userId, UserPasswordUpdateRequestDto userPasswordUpdateRequestDto);
	CommonResponseDto updateBankAccount(Long userId, UserBankUpdateRequestDto userBankUpdateRequestDto);
}

