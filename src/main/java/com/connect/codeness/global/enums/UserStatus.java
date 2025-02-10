package com.connect.codeness.global.enums;

public enum UserStatus {
	LEAVE("탈퇴함"), ACTIVE("활동 중");

	private final String userStatusText;


	UserStatus(String userStatusText) {
		this.userStatusText = userStatusText;
	}
}
