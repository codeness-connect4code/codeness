package com.connect.codeness.global.enums;

public enum BookedStatus {
	EMPTY("미예약"),
	BOOKED("예약완료");

	private final String statusText;

	BookedStatus(String statusText) {
		this.statusText = statusText;
	}
}
