package com.connect.codeness.domain.mentoringpost.dto;


import com.connect.codeness.global.enums.FieldType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentoringPostResponseDto {

	/**
	 * TODO : 사용 안되면 지우기
	 */

	private Long id;//멘토링 공고 고유 식별자

	private String userNickname;//멘토 닉네임

	private FieldType field; //분야

	private String title;//공고 제목

	private String company;//회사 이름

	private Integer career;//경력

	private String region;//지역

	private BigDecimal price;//가격

	private LocalDate startDate;//멘토링 시작 날짜

	private LocalDate endDate;//멘토링 마감 날짜

	private LocalTime startTime;//멘토링 시작 시간

	private LocalTime endTime;//멘토링 마감 시간

	private String description;//설명글
}
