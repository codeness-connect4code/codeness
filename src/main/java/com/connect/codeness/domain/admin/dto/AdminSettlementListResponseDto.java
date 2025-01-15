package com.connect.codeness.domain.admin.dto;

import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class AdminSettlementListResponseDto {

	private Long mentorId;
	private String mentorName;
	private Long count;
	private BigDecimal totalCost;

	public AdminSettlementListResponseDto(Long mentorId, String mentorName, Long count, BigDecimal totalCost) {
		this.mentorId = mentorId;
		this.mentorName = mentorName;
		this.count = count;
		this.totalCost = totalCost;
	}
}
