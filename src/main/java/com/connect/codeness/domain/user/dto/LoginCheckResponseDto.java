package com.connect.codeness.domain.user.dto;

import com.connect.codeness.global.enums.UserProvider;
import com.connect.codeness.global.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginCheckResponseDto {
	private Long id;
	private UserProvider provider;
	private UserRole role;
}
