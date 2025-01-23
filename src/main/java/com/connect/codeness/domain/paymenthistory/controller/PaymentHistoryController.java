package com.connect.codeness.domain.paymenthistory.controller;


import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;
import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryResponseDto;
import com.connect.codeness.domain.paymenthistory.service.PaymentHistoryService;
import com.connect.codeness.global.jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentHistoryController {

	private final JwtUtil jwtUtil;
	private final PaymentHistoryService paymentHistoryService;

	public PaymentHistoryController(JwtUtil jwtUtil, PaymentHistoryService paymentHistoryService) {
		this.jwtUtil = jwtUtil;
		this.paymentHistoryService = paymentHistoryService;
	}

	/**
	 * - 로그인한 유저의 결제 내역 전체 & 단건 조회
	 * - 특정 유저의 결제 내역 단건 조회 -> 어드민
	 * - 전체 유저 결제 내역 조회 -> 어드민
	 */

	/**
	 * 결제내역 전체 조회 API
	 * - 멘티 & 멘토별 응답 다름
	 */
	@GetMapping("/mentoring/payment-history")
	public ResponseEntity<CommonResponseDto<?>> getAllPaymentHistory(@RequestHeader(AUTHORIZATION) String token) {
		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto<?> responseDto = paymentHistoryService.getAllPaymentHistory(userId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제내역 단건 상세 조회 API
	 * - 멘티 & 멘토 응답 똑같음 - 멘티의 결제 내역
	 */
	@GetMapping("/mentoring/payment-history/{paymentHistoryId}")
	public ResponseEntity<CommonResponseDto> getPaymentHistory(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long paymentHistoryId) {
		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto<PaymentHistoryResponseDto> responseDto = paymentHistoryService.getPaymentHistory(userId, paymentHistoryId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
	
}
