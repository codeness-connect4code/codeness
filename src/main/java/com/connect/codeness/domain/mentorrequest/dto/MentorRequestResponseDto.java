package com.connect.codeness.domain.mentorrequest.dto;

import com.connect.codeness.domain.mentorrequest.MentorRequest;
import com.connect.codeness.global.enums.FieldType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MentorRequestResponseDto {

	private String userName;
	private LocalDateTime createdAt;

	public MentorRequestResponseDto(MentorRequest mentorRequest) {
		this.userName = mentorRequest.getUser().getName();
		this.createdAt = mentorRequest.getCreatedAt();
	}

}
