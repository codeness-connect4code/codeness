package com.connect.codeness.domain.admin.dto;

import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import lombok.Getter;

@Getter
public class AdminSettlementGetResponseDto {

	private Long mentorId;
	private String mentorName;

	public AdminSettlementGetResponseDto(PaymentHistory paymentHistory) {
		this.mentorId = paymentHistory.getUser().getId();
		this.mentorName = paymentHistory.getUser().getName();
	}
}
