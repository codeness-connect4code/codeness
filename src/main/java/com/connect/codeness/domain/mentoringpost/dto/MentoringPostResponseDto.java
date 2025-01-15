package com.connect.codeness.domain.mentoringpost.dto;


import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.enums.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * TODO : 임시로 만들어 둔 dto, 필요 없을 시 삭제 예정
 */
@Getter
@Builder
public class MentoringPostResponseDto {

	@NotBlank
	private Long id;//멘토링 공고 고유 식별자

	@NotBlank
	private String userNickname;//사용자 고유 식별자 (외래키)

	@NotNull
	private FieldType field; //분야

	@NotNull
	private String title;//공고 제목

	@NotNull
	private String company;//회사 이름

	@NotNull
	private Integer career;//경력

	@NotNull
	private String region;//지역

	@NotNull
	private BigDecimal price;//가격

	@NotNull
	private LocalDate startDate;//멘토링 시작 날짜

	@NotNull
	private LocalDate endDate;//멘토링 마감 날짜

	@NotNull
	private LocalTime startTime;//멘토링 시작 시간

	@NotNull
	private LocalTime endTime;//멘토링 마감 시간

	@NotNull
	private String description;//설명글
}
