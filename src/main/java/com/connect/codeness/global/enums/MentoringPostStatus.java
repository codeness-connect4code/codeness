package com.connect.codeness.global.enums;

public enum MentoringPostStatus {
	DELETED("삭제"),
	DISPLAYED("존재");

	private final String CommentStatusText;

	MentoringPostStatus(String CommentStatusText) {
		this.CommentStatusText = CommentStatusText;
	}
}
