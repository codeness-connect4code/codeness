package com.connect.codeness.domain.settlement.service;

import com.connect.codeness.global.dto.CommonResponseDto;

public interface SettlementService {

	/**
	 * 결제내역 정산 신청 메서드
	 */
	CommonResponseDto requestSettlement(Long userId);

}
