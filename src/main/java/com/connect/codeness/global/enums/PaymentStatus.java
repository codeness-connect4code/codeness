package com.connect.codeness.global.enums;

public enum PaymentStatus {
	COMPLETE("결제완료"),
	CANCEL("결제취소");

	private final String statusText;

	PaymentStatus(String statusText) {
		this.statusText = statusText;
	}
}
