package com.connect.codeness.domain.payment;


import com.connect.codeness.domain.payment.dto.PaymentDeleteRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRefundRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRequestDto;
import com.connect.codeness.domain.paymentlist.PaymentListService;
import com.connect.codeness.global.constants.Constants;
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

	public PaymentController(PaymentService paymentService, PaymentListService paymentListService) {
		this.paymentService = paymentService;
	}

	/**
	 * 결제 생성 API
	 * - 멘토링 스케쥴 신청
	 * -TODO : 로그인 구현 완료 후 로그인 한 userId 받아오기
	 */
	@PostMapping("/payments")
	public ResponseEntity<CommonResponseDto> createPayment(@RequestHeader(Constants.AUTHORIZATION) @Valid @RequestBody PaymentRequestDto requestDto){
		CommonResponseDto responseDto = paymentService.createPayment(1L, requestDto);

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
	 */
	@PostMapping("/payments/{paymentId}/verify")
	public ResponseEntity<CommonResponseDto> verifyPayment(@PathVariable Long paymentId, @Valid @RequestBody PaymentRequestDto requestDto){

		CommonResponseDto responseDto = paymentService.verifyPayment(paymentId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제 환불 API
	 */
	@PostMapping("/payments/{paymentId}/refund")
	public ResponseEntity<CommonResponseDto> refundPayment(@PathVariable Long paymentId, @Valid @RequestBody PaymentRefundRequestDto requestDto){
	CommonResponseDto responseDto = paymentService.refundPayment(paymentId, requestDto);

	return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

}
