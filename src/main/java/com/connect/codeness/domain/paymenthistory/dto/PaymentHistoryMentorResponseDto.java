package com.connect.codeness.domain.paymenthistory.dto;

import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import com.connect.codeness.domain.settlement.Settlement;
import com.connect.codeness.global.enums.SettlementStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentHistoryMentorResponseDto {

	private Long id;//결제 내역 고유 식별자

	private Long paymentId;//결제 고유 식별자 (외래키)

	private Long mentorId;//사용자 고유 식별자 (외래키)

	private SettlementStatus settlementStatus; //정산 상태

	private LocalDateTime canceledAt; //결제 취소일

	private Long mentoringPostId; //멘토링 공고 고유 식별자

	private String userNickname;//멘토 닉네임

	private String title; //멘토링 공고 제목

	private LocalDate mentoringDate; //멘토링 스케쥴 날짜

	private LocalTime mentoringTime; //멘토링 스케쥴 시간

	/**
	 * 결제 전체 조회 메서드 사용
	 * 멘토 응답 DTO
	 * paymentHistory 객체 -> PaymentHistoryResponseDto 변환
	 */
	public static PaymentHistoryMentorResponseDto from(PaymentHistory paymentHistory){
		//정산 가져오기
		Settlement settlement = paymentHistory.getSettlement();
		SettlementStatus settlementStatus = (settlement != null) ? settlement.getSettlementStatus() : SettlementStatus.UNPROCESSED;

		return PaymentHistoryMentorResponseDto.builder()
			.id(paymentHistory.getId())
			.paymentId(paymentHistory.getPayment().getId())
			.mentorId(paymentHistory.getUser().getId())
			.settlementStatus(settlementStatus)//default 값 처리
			.canceledAt(paymentHistory.getCanceledAt())
			.mentoringPostId(paymentHistory.getPayment().getMentoringSchedule().getMentoringPost().getId())
			.userNickname(paymentHistory.getPayment().getUser().getUserNickname())
			.title(paymentHistory.getPayment().getMentoringSchedule().getMentoringPost().getTitle())
			.mentoringDate(paymentHistory.getPayment().getMentoringSchedule().getMentoringDate())
			.mentoringTime(paymentHistory.getPayment().getMentoringSchedule().getMentoringTime())
			.build();
	}

}
