package com.connect.codeness.global.enums;

public enum PostType {
	FREE("자유게시판"),
	QUESTION("질문게시판"),
	NOTICE("공지사항");

	private final String postTypeText;

	PostType(String postTypeText) {
		this.postTypeText = postTypeText;
	}
}
