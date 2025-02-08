package com.connect.codeness.global.enums;

public enum UserProvider {
	GOOGLE("구글"), 
	LOCAL("로컬");

	private final String statusText;

	UserProvider(String statusText) {
		this.statusText = statusText;
	}
}
