package com.connect.codeness.domain.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AdminSettlementListResponseDto {

	private Long mentorId;
	private String mentorName;
	private Long count;
	private BigDecimal totalCost;
	private LocalDateTime createAt;
	private String account;
	private BigDecimal charge;
	private BigDecimal finalCost;

	public AdminSettlementListResponseDto(Long mentorId, String mentorName, Long count, BigDecimal totalCost, LocalDateTime createAt, String account) {
		this.mentorId = mentorId;
		this.mentorName = mentorName;
		this.count = count;
		this.totalCost = totalCost;
		this.createAt = createAt;
		this.account = account;
		this.charge = totalCost.multiply(BigDecimal.valueOf(0.04));
		this.finalCost = totalCost.subtract(this.charge);
	}
}
