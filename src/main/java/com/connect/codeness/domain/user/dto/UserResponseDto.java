package com.connect.codeness.domain.user.dto;

import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.enums.FieldType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {
	private String name;
	private String userNickname;
	private String email;
	private String phoneNumber;
	private String region;
	private FieldType field;
	private Integer career;
	private String mbti;
	private String siteLink;
	private String profileImageLink;

	public UserResponseDto(User user, String profileImageLink) {
		this.name = user.getName();
		this.userNickname = user.getUserNickname();
		this.email = user.getEmail();
		this.phoneNumber = user.getPhoneNumber();
		this.region = user.getRegion();
		this.field = user.getField();
		this.career = user.getCareer();
		this.mbti = user.getMbti();
		this.siteLink = user.getSiteLink();
		this.profileImageLink = profileImageLink;
	}

	@Builder
	public UserResponseDto(String email, String name, String userNickname,
		String siteLink, FieldType field, Integer career,
		String region, String mbti, String phoneNumber, String profileImageLink) {
		this.email = email;
		this.name = name;
		this.userNickname = userNickname;
		this.siteLink = siteLink;
		this.field = field;
		this.career = career;
		this.region = region;
		this.mbti = mbti;
		this.phoneNumber = phoneNumber;
		this.profileImageLink = profileImageLink;
	}
}
