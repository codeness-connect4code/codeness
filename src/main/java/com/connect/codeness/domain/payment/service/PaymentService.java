package com.connect.codeness.domain.payment.service;


import com.connect.codeness.domain.payment.dto.PaymentDeleteRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRefundRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface PaymentService {

	/**
	 * 결제 생성 메서드
	 * - 멘토링 스케쥴 신청
	 */
	CommonResponseDto<?> createPayment(Long userId, PaymentRequestDto requestDto);

	/**
	 * 결제 삭제 서비스 메서드
	 * - 클라이언트에서 결제 도중 취소하거나 결제 창을 빠져나가 결제가 중단 되었을 경우
	 * - paymentId만 가지고 데이터 삭제를 진행 - 데이터 저장해 둘 필요가 없음
	 */
	CommonResponseDto<?> deletePaymentOnCancel(Long paymentId);

	/**
	 * 결제 삭제 서비스 메서드
	 * - 결제 진행시, 잔액 부족 등으로 결제가 거절되었을 경우 결제 데이터 삭제
	 */
	CommonResponseDto<?> deletePaymentOnRejection(Long paymentId, PaymentDeleteRequestDto requestDto);

	/**
	 * 결제 검증 메서드
	 */
	CommonResponseDto<PaymentResponseDto> verifyPayment(Long paymentId, PaymentRequestDto requestDto);

	/**
	 * 결제 환불 메서드
	 */
	CommonResponseDto<PaymentResponseDto> refundPayment(Long userId, Long paymentId, PaymentRefundRequestDto requestDto);
}

