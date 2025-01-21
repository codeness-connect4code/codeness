package com.connect.codeness.domain.paymenthistory.dto;

import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.global.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentHistoryResponseDto {

	private Long id;//결제 내역 고유 식별자

	private Long paymentId;//결제 고유 식별자 (외래키)

	private Long mentorId;//사용자 고유 식별자 (멘토)

	private BigDecimal paymentCost; //결제 금액

	private String paymentCard; //결제 카드 정보

	private PaymentStatus paymentStatus;//결제 상태

	private LocalDateTime createdAt; //멘토링 스케쥴 결제 일시

	private LocalDateTime canceledAt; //결제 취소일

	private String userNickname;//멘토 닉네임

	private String title; //멘토링 공고 제목

	private LocalDate mentoringDate; //멘토링 스케쥴 날짜

	private LocalTime mentoringTime; //멘토링 스케쥴 시간

	/**
	 * 결제 단건 상세 조회 메서드 사용
	 * 정적 메서드
	 * paymentHistory 객체 -> PaymentHistoryResponseDto 변환
	 */
	public static PaymentHistoryResponseDto from(PaymentHistory paymentHistory) {
		return PaymentHistoryResponseDto.builder()
			.id(paymentHistory.getId())
			.paymentId(paymentHistory.getPayment().getId())
			.mentorId(paymentHistory.getUser().getId())
			.paymentCost(paymentHistory.getPaymentCost())
			.paymentCard(paymentHistory.getPaymentCard())
			.paymentStatus(paymentHistory.getPaymentStatus())
			.createdAt(paymentHistory.getCreatedAt())
			.canceledAt(paymentHistory.getCanceledAt())
			.userNickname(paymentHistory.getPayment().getUser().getUserNickname())
			.title(paymentHistory.getPayment().getMentoringSchedule().getMentoringPost().getTitle())
			.mentoringDate(paymentHistory.getPayment().getMentoringSchedule().getMentoringDate())
			.mentoringTime(paymentHistory.getPayment().getMentoringSchedule().getMentoringTime())
			.build();
	}
}
