package com.connect.codeness.domain.paymentlist;


import com.connect.codeness.domain.paymentlist.dto.PaymentListResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;

public interface PaymentListService {

	/**
	 * 결제내역 전체 조회 메서드
	 */
	CommonResponseDto<List<PaymentListResponseDto>> getAllPaymentHistory(Long userId);

	/**
	 * 결제내역 단건 조회 메서드
	 */
	CommonResponseDto<PaymentListResponseDto> getPaymentHistory(Long userId, Long paymentListId);

	/**
	 * 결제내역 정산 신청 메서드
	 */
	CommonResponseDto requestSettlement(Long userId);
}

