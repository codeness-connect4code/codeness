package com.connect.codeness.domain.mentoringpost.dto;


import com.connect.codeness.global.enums.FieldType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MentoringPostCreateRequestDto {

	@NotNull
	private final FieldType field; //분야

	@NotNull
	@Size(max = 30, message = "공고 제목은 {max}자 이내로 작성이 가능합니다.")
	private final String title;//공고 제목

	@NotNull
	@Size(max = 20, message = "회사 이름은 {max}자 이내로 작성이 가능합니다.")
	private final String company;//회사 이름

	@NotNull
	@Max(value = 70, message = "경력은 숫자 {value} 이내로 작성이 가능합니다.")
	private final Integer career;//경력

	@NotNull
	@Size(max = 30, message = "지역은 {max}자 이내로 작성이 가능합니다.")
	private final String region;//지역

	@NotNull
	@DecimalMax(value = "100000", message = "시간당 설정 가능한 최대 가격은 {value}원입니다.")
	private final BigDecimal price;//가격

	@NotNull
	private final LocalDate startDate;//멘토링 시작 날짜

	@NotNull
	private final LocalDate endDate;//멘토링 마감 날짜

	@NotNull
	private final LocalTime startTime;//멘토링 시작 시간

	@NotNull
	private final LocalTime endTime;//멘토링 마감 시간

	@NotNull
	@Size(max = 300, message = "설명글은 {max}자 이내로 작성이 가능합니다.")
	private final String description;//설명글

}
