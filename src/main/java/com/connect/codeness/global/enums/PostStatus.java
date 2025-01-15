package com.connect.codeness.global.enums;

public enum PostStatus {
	DELETED("삭제"),
	DISPLAYED("존재");

	private final String PostStatusText;

	PostStatus(String PostStatusText) {
		this.PostStatusText = PostStatusText;
	}
}
