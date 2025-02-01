package com.connect.codeness.global.enums;

public enum BookedStatus {
	EMPTY("미예약"),
	IN_PROGRESS("예약 진행중"),
	BOOKED("예약완료");

	private final String statusText;

	BookedStatus(String statusText) {
		this.statusText = statusText;
	}
}
