package com.connect.codeness.global.enums;

public enum MentoringScheduleStatus {
	DELETED("삭제"),
	DISPLAYED("존재"),
	EXPIRED("만료");

	private final String MentoringScheduleStatusText;

	MentoringScheduleStatus(String mentoringScheduleStatusText) {
		MentoringScheduleStatusText = mentoringScheduleStatusText;
	}
}
