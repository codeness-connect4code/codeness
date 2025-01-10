package com.connect.codeness.domain.user.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
	private String email;
	private String password;

	public LoginRequestDto() {}
}
