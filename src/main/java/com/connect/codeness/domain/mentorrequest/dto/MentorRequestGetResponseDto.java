package com.connect.codeness.domain.mentorrequest.dto;

import com.connect.codeness.domain.mentorrequest.entity.MentorRequest;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.MentorRequestStatus;
import lombok.Getter;

@Getter
public class MentorRequestGetResponseDto {
	private Long requestId;
	private String position;
	private Integer career;
	private FieldType field;
	private MentorRequestStatus isAccepted;

	public MentorRequestGetResponseDto(MentorRequest mentorRequest) {
		this.requestId = mentorRequest.getId();
		this.position = mentorRequest.getPosition();
		this.career = mentorRequest.getCareer();
		this.field = mentorRequest.getField();
		this.isAccepted = mentorRequest.getIsAccepted();
	}
}
