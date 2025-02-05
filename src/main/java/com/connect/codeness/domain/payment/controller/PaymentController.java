package com.connect.codeness.domain.payment.controller;


import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.payment.dto.PaymentDeleteRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRefundRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRequestDto;
import com.connect.codeness.domain.payment.service.PaymentService;
import com.connect.codeness.domain.paymenthistory.service.PaymentHistoryService;
import com.connect.codeness.global.jwt.JwtProvider;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

	private final PaymentService paymentService;
	private final JwtProvider jwtProvider;

	public PaymentController(PaymentService paymentService, PaymentHistoryService paymentHistoryService, JwtProvider jwtProvider) {
		this.paymentService = paymentService;
		this.jwtProvider = jwtProvider;
	}

	/**
	 * 결제 생성 API
	 * - 멘토링 스케쥴 신청
	 */
	@PostMapping("/payments/mentoring-schedules")
	public ResponseEntity<CommonResponseDto<?>> createPayment(@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestBody PaymentRequestDto requestDto) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<?> responseDto = paymentService.createPayment(userId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	/**
	 * 결제 취소시 삭제 API
	 * - 클라이언트에서 결제 도중 취소하거나 결제 창을 빠져나가 결제가 중단 되었을 경우
	 */
	@DeleteMapping("/payments/{paymentId}/cancel")
	public ResponseEntity<CommonResponseDto<?>> deletePaymentOnCancel(@PathVariable Long paymentId) {

		CommonResponseDto<?> responseDto = paymentService.deletePaymentOnCancel(paymentId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제 거절시 삭제 API
	 * - 결제 진행시, 잔액 부족 등으로 결제가 거절되었을 경우 결제 데이터 삭제
	 */
	@DeleteMapping("/payments/{paymentId}/rejection")
	public ResponseEntity<CommonResponseDto<?>> deletePaymentOnRejection(@PathVariable Long paymentId,
		@Valid @RequestBody PaymentDeleteRequestDto requestDto) {

		CommonResponseDto<?> responseDto = paymentService.deletePaymentOnRejection(paymentId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제 검증 API
	 */
	@PostMapping("/payments/{paymentId}/verify")
	public ResponseEntity<CommonResponseDto<?>> verifyPayment(@PathVariable Long paymentId,
		@Valid @RequestBody PaymentRequestDto requestDto) {

		CommonResponseDto<?> responseDto = paymentService.verifyPayment(paymentId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제 환불 API
	 */
	@PostMapping("/payments/{paymentId}/refund")
	public ResponseEntity<CommonResponseDto<?>> refundPayment(@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@PathVariable Long paymentId, @Valid @RequestBody PaymentRefundRequestDto requestDto) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<?> responseDto = paymentService.refundPayment(userId, paymentId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

}
