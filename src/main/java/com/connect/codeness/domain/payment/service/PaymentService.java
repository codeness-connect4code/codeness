package com.connect.codeness.domain.payment.service;


import com.connect.codeness.domain.payment.dto.PaymentDeleteRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRefundRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface PaymentService {

	/**
	 * 결제 생성 메서드
	 * - 멘토링 스케쥴 신청
	 */
	CommonResponseDto createPayment(Long userId, PaymentRequestDto requestDto);

	/**
	 * 결제 삭제 메서드
	 * - 결제 도중 취소하거나 결제가 거절됐을 경우 결제 데이터 삭제
	 */
	CommonResponseDto deletePayment(Long paymentId, PaymentDeleteRequestDto requestDto);

	/**
	 * 결제 검증 메서드
	 */
	CommonResponseDto verifyPayment(Long paymentId, PaymentRequestDto requestDto);

	/**
	 * 결제 환불 메서드
	 */
	CommonResponseDto refundPayment(Long userId, Long paymentId, PaymentRefundRequestDto requestDto);
}

