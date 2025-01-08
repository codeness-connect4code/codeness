package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.UserCreateRequestDto;

public interface UserService {
	void createUser(UserCreateRequestDto userCreateRequestDto);
}

