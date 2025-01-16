package com.connect.codeness.domain.settlement;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.paymenthistory.PaymentHistoryService;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.connect.codeness.global.jwt.JwtUtil;

@RestController
public class SettlementController {

	private final JwtUtil jwtUtil;
	private final SettlementService settlementService;

	public SettlementController(JwtUtil jwtUtil, SettlementService settlementService) {
		this.jwtUtil = jwtUtil;
		this.settlementService = settlementService;
	}

	/**
	 * 결제내역 정산 신청 API
	 * - 멘토
	 * - 정산상태 변경
	 * - TODO : 정산 도메인으로 옮기기
	 */
	@PatchMapping("/mentors/mentoring/payment-history/settles")
	public ResponseEntity<CommonResponseDto> requestSettlement(@RequestHeader(AUTHORIZATION) String token){
		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto responseDto = settlementService.requestSettlement(userId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

}
