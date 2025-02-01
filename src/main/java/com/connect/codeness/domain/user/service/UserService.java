package com.connect.codeness.domain.user.service;

import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.user.dto.GoogleUserUpdateRequestDto;
import com.connect.codeness.domain.user.dto.LoginRequestDto;
import com.connect.codeness.domain.user.dto.UserBankUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserCreateRequestDto;
import com.connect.codeness.domain.user.dto.UserDeleteResponseDto;
import com.connect.codeness.domain.user.dto.UserPasswordUpdateRequestDto;
import com.connect.codeness.domain.user.dto.UserUpdateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserService {
	CommonResponseDto<?> createUser(UserCreateRequestDto userCreateRequestDto);
	String login(LoginRequestDto loginRequestDto, HttpServletResponse response) throws IOException;
	CommonResponseDto<?> getUser(Long userId);
	CommonResponseDto<?> updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto, ImageFile imageFile) throws IOException;
	CommonResponseDto<?> updateGoogleUser(Long userId, GoogleUserUpdateRequestDto googleUserUpdateRequestDto, ImageFile imageFile) throws IOException;
	CommonResponseDto<?> updatePassword(Long userId, UserPasswordUpdateRequestDto userPasswordUpdateRequestDto);
	CommonResponseDto<?> updateBankAccount(Long userId, UserBankUpdateRequestDto userBankUpdateRequestDto);
	CommonResponseDto<?> deleteUser(Long userId, UserDeleteResponseDto userDeleteResponseDto);
	CommonResponseDto<?> getMentoring(Long userId);
	CommonResponseDto<?> loginCheck(Long userId);
}

