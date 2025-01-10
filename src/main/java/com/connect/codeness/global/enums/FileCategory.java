package com.connect.codeness.global.enums;

import lombok.Getter;


public enum FileCategory {
	PROFILE("Profile"),
	EMPLOYEE_CARD("EmployeeCard"),;

	@Getter
	private final String categoryText;

	FileCategory(String categoryText) {
		this.categoryText = categoryText;
	}
}
