package com.connect.codeness.domain.admin.dto;

import com.connect.codeness.domain.user.entity.User;
import lombok.Getter;

@Getter
public class AdminMentorListResponseDto {

	private Long userId;
	private String name;

	public AdminMentorListResponseDto(User user) {
		this.name = user.getName();
		this.userId = user.getId();
	}
}
