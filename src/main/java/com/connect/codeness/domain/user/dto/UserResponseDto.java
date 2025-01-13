package com.connect.codeness.domain.user.dto;

import com.connect.codeness.global.enums.FieldType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponseDto {
	private String name;
	private String nickname;
	private String email;
	private String phoneNumber;
	private String region;
	private FieldType field;
	private Integer career;
	private String mbti;
	private String siteLink;
}
