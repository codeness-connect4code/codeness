package com.connect.codeness.domain.paymenthistory;


import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryMenteeResponseDto;
import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;

public interface PaymentHistoryService {

	/**
	 * 결제내역 전체 조회 메서드
	 */
	CommonResponseDto<?> getAllPaymentHistory(Long userId);

	/**
	 * 결제내역 단건 조회 메서드
	 */
	CommonResponseDto<PaymentHistoryResponseDto> getPaymentHistory(Long userId, Long paymentHistoryId);

}

