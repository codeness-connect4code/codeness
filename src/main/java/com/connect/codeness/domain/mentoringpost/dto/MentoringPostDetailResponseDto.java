package com.connect.codeness.domain.mentoringpost.dto;


import com.connect.codeness.global.enums.FieldType;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentoringPostDetailResponseDto {
	//TODO : 공고 올린 멘토 닉네임, 공고 생성일도 응답해줘야 함

	private Long mentoringPostId;//멘토링 공고 고유 식별자

	private String userNickname;//사용자 고유 식별자 (외래키)

	private FieldType field; //분야

	private String title;//공고 제목

	private String company;//회사 이름

	private Integer career;//경력

	private String region;//지역

	private BigDecimal price;//가격

	private String description;//설명글

	private Double starRating;//평균 별점
}
