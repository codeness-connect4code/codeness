package com.connect.codeness.domain.paymenthistory.service;


import com.connect.codeness.domain.paymenthistory.dto.MentorPaymentHistoryResponseDto;
import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface PaymentHistoryService {

	/**
	 * 결제내역 전체 조회 메서드
	 */
	CommonResponseDto<?> getAllPaymentHistory(Long userId);

	/**
	 * 결제내역 단건 조회 메서드
	 * - 멘티
	 */
	CommonResponseDto<PaymentHistoryResponseDto> getPaymentHistory(Long userId, Long paymentHistoryId);

	/**
	 * 결제내역 단건 조회 메서드
	 * - 멘토
	 */
	CommonResponseDto<MentorPaymentHistoryResponseDto> getPaymentHistoryForMentor(Long userId, Long paymentHistoryId);

}

