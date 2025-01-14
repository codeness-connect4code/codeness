package com.connect.codeness.global.dto;

public class GoogleLoginDto {

	private String token;

	public GoogleLoginDto() {}

	public GoogleLoginDto(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}