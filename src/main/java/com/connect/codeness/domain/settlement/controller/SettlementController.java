package com.connect.codeness.domain.settlement.controller;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.settlement.service.SettlementService;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettlementController {

	private final JwtProvider jwtProvider;
	private final SettlementService settlementService;

	public SettlementController(JwtProvider jwtProvider, SettlementService settlementService) {
		this.jwtProvider = jwtProvider;
		this.settlementService = settlementService;
	}

	/**
	 * 결제내역 정산 신청 API
	 * - 멘토
	 * - 정산 상태, 정산 요청일
	 * - 사용자 계좌, 은행명은 유저 테이블쪽에서 가져오기
	 */
	@PatchMapping("/mentors/mentoring/payment-history/settles")
	public ResponseEntity<CommonResponseDto<?>> requestSettlement(@RequestHeader(AUTHORIZATION) String authorizationHeader) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<?> responseDto = settlementService.requestSettlement(userId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

}
