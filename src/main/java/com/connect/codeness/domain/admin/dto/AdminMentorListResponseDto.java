package com.connect.codeness.domain.admin.dto;

import com.connect.codeness.domain.user.User;
import lombok.Getter;

@Getter
public class AdminMentorListResponseDto {

	private String name;

	public AdminMentorListResponseDto(User user) {
		this.name = user.getName();
	}
}
