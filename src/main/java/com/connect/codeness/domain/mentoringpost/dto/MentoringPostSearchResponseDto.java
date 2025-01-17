package com.connect.codeness.domain.mentoringpost.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentoringPostSearchResponseDto {

	private Long mentoringPostId;//멘토링 공고 고유 식별자

	private String userNickname;//사용자 고유 식별자 (외래키)

	private String field; //분야 - 문자열로 변경

	private String title;//공고 제목

	private Integer career;//경력

//	private Double starRating;//평균 별점 - TODO : 추가


	public MentoringPostSearchResponseDto() {
	}

	public MentoringPostSearchResponseDto(Long mentoringPostId, String userNickname, String field, String title, Integer career) {
		this.mentoringPostId = mentoringPostId;
		this.userNickname = userNickname;
		this.field = field;
		this.title = title;
		this.career = career;
	}

}
