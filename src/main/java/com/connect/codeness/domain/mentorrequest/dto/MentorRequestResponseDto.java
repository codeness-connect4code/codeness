package com.connect.codeness.domain.mentorrequest.dto;

import com.connect.codeness.domain.mentorrequest.MentorRequest;
import com.connect.codeness.global.enums.FieldType;
import lombok.Getter;

@Getter
public class MentorRequestResponseDto {

	private Long mentorRequestId;
	private String userName;
	private String phoneNumber;
	private FieldType field;
	private String company;
	private String position;
	private Integer career;
	private String companyEmail;

	public MentorRequestResponseDto(MentorRequest mentorRequest) {
		this.mentorRequestId = mentorRequest.getId();
		this.userName = mentorRequest.getUser().getName();
		this.phoneNumber = mentorRequest.getPhoneNumber();
		this.field = mentorRequest.getField();
		this.company = mentorRequest.getCompany();
		this.position = mentorRequest.getPosition();
		this.career = mentorRequest.getCareer();
		this.companyEmail = mentorRequest.getCompanyEmail();
	}

}
