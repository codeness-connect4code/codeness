package com.connect.codeness.global.enums;

public enum MentorRequestStatus {
	WAITING("대기 중"), ACCEPTED("수락됨"), REJECTED("거절됨");

	private final String mentorRequestStatusText;

	MentorRequestStatus(String mentorRequestStatusText) {
		this.mentorRequestStatusText = mentorRequestStatusText;
	}
}
