package com.connect.codeness.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponseDto {

		private final Long paymentHistoryId; //결제내역 id

	public PaymentResponseDto(Long paymentHistoryId) {
		this.paymentHistoryId = paymentHistoryId;
	}
}
