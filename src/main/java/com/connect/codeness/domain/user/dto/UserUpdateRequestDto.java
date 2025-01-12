package com.connect.codeness.domain.user.dto;

import com.connect.codeness.global.enums.FieldType;
import lombok.Getter;

@Getter
public class UserUpdateRequestDto {
	private String nickname;
	private String phoneNumber;
	private String region;
	private FieldType field;
	private Integer career;
	private String mbti;
	private String siteLink;
}
