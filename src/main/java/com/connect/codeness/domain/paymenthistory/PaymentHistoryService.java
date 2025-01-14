package com.connect.codeness.domain.paymenthistory;


import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;

public interface PaymentHistoryService {

	/**
	 * 결제내역 전체 조회 메서드
	 */
	CommonResponseDto<List<PaymentHistoryResponseDto>> getAllPaymentHistory(Long userId);

	/**
	 * 결제내역 단건 조회 메서드
	 */
	CommonResponseDto<PaymentHistoryResponseDto> getPaymentHistory(Long userId, Long paymentHistoryId);

	/**
	 * 결제내역 정산 신청 메서드
	 */
	CommonResponseDto requestSettlement(Long userId);
}

