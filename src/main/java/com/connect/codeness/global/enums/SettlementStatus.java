package com.connect.codeness.global.enums;

public enum SettlementStatus {
	UNPROCESSED("미처리"),
	PROCESSING("처리중"),
	COMPLETE("정산완료");

	private final String statusText;

	SettlementStatus(String statusText) {
		this.statusText = statusText;
	}
}
