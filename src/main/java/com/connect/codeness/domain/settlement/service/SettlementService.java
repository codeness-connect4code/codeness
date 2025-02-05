package com.connect.codeness.domain.settlement.service;

import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.SettlementStatus;

public interface SettlementService {

	/**
	 * 결제내역 정산 신청 메서드
	 */
	CommonResponseDto requestSettlement(Long userId);

	/**
	 * 정산 내역 조회 메서드
	 */
	CommonResponseDto<?> getSettlement(Long userId, SettlementStatus status);
}
