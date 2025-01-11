package com.connect.codeness.domain.user.dto;

import lombok.Getter;

@Getter
public class UserPasswordUpdateRequestDto {
	private String currentPassword;
	private String newPassword;
}
