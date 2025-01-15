package com.connect.codeness.domain.admin.dto;

import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import com.connect.codeness.global.enums.PaymentStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class AdminSettlementResponseDto {

	private Long paymentHistoryId;
	 private PaymentStatus paymentState;
	 private String mentorName;
	 private LocalDate mentoringDate;
	 private LocalTime mentoringTime;

	 public AdminSettlementResponseDto(PaymentHistory paymentHistory) {
		 this.paymentHistoryId = paymentHistory.getId();
		 this.paymentState = paymentHistory.getPaymentStatus();
		 this.mentorName = paymentHistory.getUser().getName();
		 this.mentoringDate = paymentHistory.getPayment().getMentoringSchedule().getMentoringDate();
		 this.mentoringTime = paymentHistory.getPayment().getMentoringSchedule().getMentoringTime();
	 }

}
