package com.connect.codeness.domain.payment.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponseDto {

	private Long partnerId; //멘토 id

	private LocalDate mentoringDate; //멘토링 날짜

	private LocalTime mentoringTime; //멘토링 시간

}
