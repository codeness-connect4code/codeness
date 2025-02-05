package com.connect.codeness.domain.paymenthistory.controller;


import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.paymenthistory.dto.MentorPaymentHistoryResponseDto;
import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryResponseDto;
import com.connect.codeness.domain.paymenthistory.service.PaymentHistoryService;
import com.connect.codeness.global.jwt.JwtProvider;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentHistoryController {

	private final JwtProvider jwtProvider;
	private final PaymentHistoryService paymentHistoryService;

	public PaymentHistoryController(JwtProvider jwtProvider, PaymentHistoryService paymentHistoryService) {
		this.jwtProvider = jwtProvider;
		this.paymentHistoryService = paymentHistoryService;
	}

	/**
	 * 결제내역 전체 조회 API
	 * - 멘티 & 멘토별 응답 다름
	 */
	@GetMapping("/payment-history")
	public ResponseEntity<CommonResponseDto<?>> getAllPaymentHistory(@RequestHeader(AUTHORIZATION) String authorizationHeader) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<?> responseDto = paymentHistoryService.getAllPaymentHistory(userId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제내역 단건 상세 조회 API
	 * - 멘티가 조회 : 로그인한 멘티의 결제 내역
	 */
	@GetMapping("/payment-history/{paymentHistoryId}/mentees")
	public ResponseEntity<CommonResponseDto<PaymentHistoryResponseDto>> getPaymentHistoryFromMentee(@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@PathVariable Long paymentHistoryId) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<PaymentHistoryResponseDto> responseDto = paymentHistoryService.getPaymentHistory(userId, paymentHistoryId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제내역 단건 상세 조회 API
	 * - 멘토가 조회 : 멘티의 결제 내역 조회됨
	 */
	@GetMapping("/payment-history/{paymentHistoryId}/mentors")
	public ResponseEntity<CommonResponseDto<MentorPaymentHistoryResponseDto>> getPaymentHistoryForMentor(@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@PathVariable Long paymentHistoryId) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<MentorPaymentHistoryResponseDto> responseDto = paymentHistoryService.getPaymentHistoryForMentor(userId, paymentHistoryId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
