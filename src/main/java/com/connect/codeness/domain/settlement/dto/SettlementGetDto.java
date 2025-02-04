package com.connect.codeness.domain.settlement.dto;

import com.connect.codeness.global.enums.SettlementStatus;
import lombok.Getter;

@Getter
public class SettlementGetDto {
	private SettlementStatus status;

	public SettlementGetDto () {}

	public SettlementGetDto(SettlementStatus status){
		this.status = status;
	}
}
