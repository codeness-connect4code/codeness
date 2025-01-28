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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mentoring")
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
	@PostMapping("/payments")
	public ResponseEntity<CommonResponseDto> createPayment(@RequestHeader(AUTHORIZATION) String token,  @RequestBody PaymentRequestDto requestDto){
		Long userId = jwtProvider.extractUserId(token);

		CommonResponseDto responseDto = paymentService.createPayment(userId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	/**
	 * 결제 삭제 API
	 * - 결제 거절시 결제 데이터 삭제 메서드
	 */
	@DeleteMapping("/payments/{paymentId}")
	public ResponseEntity<CommonResponseDto> deletePayment(@PathVariable Long paymentId, @Valid @RequestBody PaymentDeleteRequestDto requestDto){

		CommonResponseDto responseDto = paymentService.deletePayment(paymentId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제 검증 API
	 * - 검증 완료시 채팅방 생성
	 */
	@PostMapping("/payments/{paymentId}/verify")
	public ResponseEntity<CommonResponseDto> verifyPayment(@PathVariable Long paymentId, @Valid @RequestBody PaymentRequestDto requestDto){

		CommonResponseDto responseDto = paymentService.verifyPayment(paymentId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제 환불 API
	 * - 환불 완료시 채팅방 삭제
	 */
	@PostMapping("/payments/{paymentId}/refund")
	public ResponseEntity<CommonResponseDto> refundPayment(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long paymentId, @Valid @RequestBody PaymentRefundRequestDto requestDto){
		Long userId = jwtProvider.extractUserId(token);

		CommonResponseDto responseDto = paymentService.refundPayment(userId, paymentId, requestDto);

	return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

}
