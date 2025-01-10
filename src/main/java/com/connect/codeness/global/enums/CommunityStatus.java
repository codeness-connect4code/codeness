package com.connect.codeness.global.enums;

public enum CommunityStatus {
	DELETED("삭제"),
	DISPLAYED("존재");

	private final String CommunityStatusText;

	CommunityStatus(String CommunityStatusText) {
		this.CommunityStatusText = CommunityStatusText;
	}
}
