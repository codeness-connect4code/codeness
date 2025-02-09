package com.connect.codeness.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginDto {

	private Long id;
	private String accessToken;
	private String refreshToken;

	public static String getKey(Long userId){
		return "USER:LOGIN:" + userId;
	}

}
