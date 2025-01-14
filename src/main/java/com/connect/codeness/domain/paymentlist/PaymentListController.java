package com.connect.codeness.domain.paymentlist;


import com.connect.codeness.domain.paymentlist.dto.PaymentListResponseDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.constants.Constants;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentListController {

	private final JwtUtil jwtUtil;
	private final PaymentListService paymentListService;

	public PaymentListController(JwtUtil jwtUtil, PaymentListService paymentListService) {
		this.jwtUtil = jwtUtil;
		this.paymentListService = paymentListService;
	}

	/**
	 * TODO : PaymentList -> PaymentHistory 이름 변경
	 * - 로그인한 유저의 결제 내역 전체 & 단건 조회
	 * - 특정 유저의 결제 내역 단건 조회 -> 어드민
	 * - 전체 유저 결제 내역 조회 -> 어드민
	 * - 멘티, 멘토 구분
	 */

	/**
	 * 결제내역 전체 조회 API
	 * - 멘티 & 멘토
	 */
	@GetMapping("/mentoring/payment-list")
	public ResponseEntity<CommonResponseDto<List<PaymentListResponseDto>>> getAllPaymentHistory(
		@RequestHeader(Constants.AUTHORIZATION) String token) {
		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto<List<PaymentListResponseDto>> responseDto = paymentListService.getAllPaymentHistory(userId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제내역 단건 조회 API
	 * - 멘티
	 */
	@GetMapping("/mentoring/payment-list/{paymentListId}")
	public ResponseEntity<CommonResponseDto> getPaymentHistory(@RequestHeader(Constants.AUTHORIZATION) String token, @PathVariable Long paymentListId) {
		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto<PaymentListResponseDto> responseDto = paymentListService.getPaymentHistory(userId, paymentListId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 결제내역 정산 신청 API
	 * - 멘토
	 * - 정산상태 변경
	 */
	@PatchMapping("/mentors/mentoring/payment-list/{paymentListId}/settles")
	public ResponseEntity<CommonResponseDto> requestSettlement(@RequestHeader(Constants.AUTHORIZATION) String token, @PathVariable Long paymentListId){
		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto responseDto = paymentListService.requestSettlement(userId, paymentListId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
	
}
