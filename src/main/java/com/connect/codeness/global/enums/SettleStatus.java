package com.connect.codeness.global.enums;

public enum SettleStatus {
	UNPROCESSED("미처리"),
	PROCESSING("처리중"),
	COMPLETE("정산완료");

	private final String statusText;

	SettleStatus(String statusText) {
		this.statusText = statusText;
	}
}
