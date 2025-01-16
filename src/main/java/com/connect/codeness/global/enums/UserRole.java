package com.connect.codeness.global.enums;

public enum UserRole {
	ADMIN("관리자"),MENTOR("멘토"),MENTEE("멘티");

	private final String userRoleText;

	UserRole(String userRoleText) {
		this.userRoleText = userRoleText;
	}
}
