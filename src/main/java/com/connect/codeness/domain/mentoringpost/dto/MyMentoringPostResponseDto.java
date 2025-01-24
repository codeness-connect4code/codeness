package com.connect.codeness.domain.mentoringpost.dto;


import com.connect.codeness.global.enums.FieldType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyMentoringPostResponseDto {

	private Long mentoringPostId;//멘토링 공고 고유 식별자

	private String userNickname;//멘토 닉네임

	private FieldType field; //분야

	private String title;//공고 제목

	private Integer career;//경력

	private Double starRating;//평균 별점

	public MyMentoringPostResponseDto() {
	}

	public MyMentoringPostResponseDto(Long mentoringPostId, String userNickname, FieldType field, String title, Integer career, Double starRating) {
		this.mentoringPostId = mentoringPostId;
		this.userNickname = userNickname;
		this.field = field;
		this.title = title;
		this.career = career;
		this.starRating = starRating;
	}
}
