package com.connect.codeness.domain.mentoringpost.dto;


import com.connect.codeness.global.enums.FieldType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentoringPostDetailResponseDto {

	private Long mentoringPostId;//멘토링 공고 고유 식별자

	private String userNickname;//멘토 닉네임

	private FieldType field; //분야

	private String title;//공고 제목

	private String company;//회사 이름

	private Integer career;//경력

	private String region;//지역

	private BigDecimal price;//가격

	private String description;//설명글

	private Double starRating;//평균 별점

	private LocalDateTime createdAt; //공고 생성일
}
