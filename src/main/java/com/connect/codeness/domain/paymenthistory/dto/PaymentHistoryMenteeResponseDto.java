package com.connect.codeness.domain.paymenthistory.dto;

import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.global.enums.PaymentStatus;
import com.connect.codeness.global.enums.ReviewStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentHistoryMenteeResponseDto {

	private Long id;//결제 내역 고유 식별자

	private Long paymentId;//결제 고유 식별자 (외래키)

	private Long mentorId;//사용자 고유 식별자 (멘토)

	private PaymentStatus paymentStatus;//결제 상태

	private ReviewStatus reviewStatus; //후기 작성 상태

	private Long mentoringPostId; //멘토링 공고 고유 식별자

	private String userNickname;//멘토 닉네임

	private String title; //멘토링 공고 제목

	private LocalDate mentoringDate; //멘토링 스케쥴 날짜

	private LocalTime mentoringTime; //멘토링 스케쥴 시간

	/**
	 * 결제 전체 조회 메서드 사용
	 * 멘티 응답 DTO
	 * paymentHistory 객체 -> PaymentHistoryResponseDto 변환
	 */
	public static PaymentHistoryMenteeResponseDto from(PaymentHistory paymentHistory){
		return PaymentHistoryMenteeResponseDto.builder()
			.id(paymentHistory.getId())
			.paymentId(paymentHistory.getPayment().getId())
			.mentorId(paymentHistory.getUser().getId())
			.paymentStatus(paymentHistory.getPaymentStatus())
			.reviewStatus(paymentHistory.getReviewStatus())
			.mentoringPostId(paymentHistory.getPayment().getMentoringSchedule().getMentoringPost().getId())
			.userNickname(paymentHistory.getPayment().getUser().getUserNickname())
			.title(paymentHistory.getPayment().getMentoringSchedule().getMentoringPost().getTitle())
			.mentoringDate(paymentHistory.getPayment().getMentoringSchedule().getMentoringDate())
			.mentoringTime(paymentHistory.getPayment().getMentoringSchedule().getMentoringTime())
			.build();
	}
}
