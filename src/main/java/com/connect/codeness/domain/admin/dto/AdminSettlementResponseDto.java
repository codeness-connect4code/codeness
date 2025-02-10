package com.connect.codeness.domain.admin.dto;

import com.connect.codeness.domain.settlement.entity.Settlement;
import com.connect.codeness.global.enums.SettlementStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class AdminSettlementResponseDto {

	private Long settlementId;
	private SettlementStatus settlementStatus;
	private String mentorName;
	private LocalDate mentoringDate;
	private LocalTime mentoringTime;

	public AdminSettlementResponseDto(Settlement s) {
		this.settlementId = s.getId();
		this.settlementStatus = s.getSettlementStatus();
		this.mentorName = s.getUser().getName();
		this.mentoringDate = s.getPaymentHistory().getPayment().getMentoringSchedule()
			.getMentoringDate();
		this.mentoringTime = s.getPaymentHistory().getPayment().getMentoringSchedule()
			.getMentoringTime();
	}

}
