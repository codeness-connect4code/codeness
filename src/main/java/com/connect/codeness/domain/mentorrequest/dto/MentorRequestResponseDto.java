package com.connect.codeness.domain.mentorrequest.dto;

import com.connect.codeness.domain.mentorrequest.entity.MentorRequest;
import com.connect.codeness.global.enums.FieldType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MentorRequestResponseDto {

	private Long mentorId;
	private String userName;
	private String phoneNumber;
	private FieldType field;
	private String company;
	private String position;
	private Integer career;
	private String companyEmail;
	private LocalDateTime createdAt;

	@Builder
	public MentorRequestResponseDto(MentorRequest mentorRequest) {
		this.mentorId = mentorRequest.getId();
		this.userName = mentorRequest.getUser().getName();
		this.createdAt = mentorRequest.getCreatedAt();
		this.phoneNumber = mentorRequest.getPhoneNumber();
		this.field = mentorRequest.getField();
		this.company = mentorRequest.getCompany();
		this.position = mentorRequest.getPosition();
		this.career = mentorRequest.getCareer();
		this.companyEmail = mentorRequest.getCompanyEmail();
	}
}
