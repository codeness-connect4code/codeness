package com.connect.codeness.domain.user;

import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserDeleteResponseDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.io.IOException;

public interface UserService {
	CommonResponseDto createUser(UserCreateRequestDto userCreateRequestDto);
	String login(LoginRequestDto loginRequestDto);
	CommonResponseDto getUser(Long userId);
	CommonResponseDto updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto, ImageFile imageFile) throws IOException;
	CommonResponseDto updatePassword(Long userId, UserPasswordUpdateRequestDto userPasswordUpdateRequestDto);
	CommonResponseDto updateBankAccount(Long userId, UserBankUpdateRequestDto userBankUpdateRequestDto);
	CommonResponseDto deleteUser(Long userId, UserDeleteResponseDto userDeleteResponseDto);
}

