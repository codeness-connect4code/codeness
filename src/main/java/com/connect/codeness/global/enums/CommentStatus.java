package com.connect.codeness.global.enums;

public enum CommentStatus {
	DELETED("삭제"),
	DISPLAYED("존재");

	private final String CommentStatusText;

	CommentStatus(String CommentStatusText) {
		this.CommentStatusText = CommentStatusText;
	}
}
