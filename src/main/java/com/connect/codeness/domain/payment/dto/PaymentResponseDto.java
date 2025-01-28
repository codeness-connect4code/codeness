package com.connect.codeness.domain.payment.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponseDto {

	private final Long partnerId; //멘토 id

	private final LocalDate mentoringDate; //멘토링 날짜

	private final LocalTime mentoringTime; //멘토링 시간

	public PaymentResponseDto(Long partnerId, LocalDate mentoringDate, LocalTime mentoringTime) {
		this.partnerId = partnerId;
		this.mentoringDate = mentoringDate;
		this.mentoringTime = mentoringTime;
	}
}
