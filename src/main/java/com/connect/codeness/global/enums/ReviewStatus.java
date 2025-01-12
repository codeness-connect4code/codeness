package com.connect.codeness.global.enums;

public enum ReviewStatus {
	NOT_YET("미작성"),
	COMPLETE("작성완료");

	private final String statusText;


	ReviewStatus(String statusText){
		this.statusText = statusText;
	}
}
