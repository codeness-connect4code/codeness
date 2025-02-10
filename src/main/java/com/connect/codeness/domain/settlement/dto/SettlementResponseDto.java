package com.connect.codeness.domain.settlement.dto;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;

@Getter
public class SettlementResponseDto {
	private Long count;
	private BigDecimal totalCost;
	private String account;
	private BigDecimal charge;
	private BigDecimal finalCost;

	public SettlementResponseDto () {}

	public SettlementResponseDto(Long count, BigDecimal totalCost, String account) {
		this.count = count;
		this.totalCost = Objects.requireNonNullElse(totalCost, BigDecimal.ZERO);
		this.account = account;

		// null 안전한 계산
		this.charge = this.totalCost.multiply(BigDecimal.valueOf(0.04));
		this.finalCost = this.totalCost.subtract(this.charge);
	}
}