package com.connect.codeness.domain.admin.dto;

import com.connect.codeness.domain.settlement.Settlement;
import com.connect.codeness.global.enums.SettleStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class AdminSettlementResponseDto {

	private Long settlementId;
	private SettleStatus settleStatus;
	private String mentorName;
	private LocalDate mentoringDate;
	private LocalTime mentoringTime;

	public AdminSettlementResponseDto(Settlement s) {
		this.settlementId = s.getId();
		this.settleStatus = s.getSettleStatus();
		this.mentorName = s.getUser().getName();
		this.mentoringDate = s.getPaymentHistory().getPayment().getMentoringSchedule()
			.getMentoringDate();
		this.mentoringTime = s.getPaymentHistory().getPayment().getMentoringSchedule()
			.getMentoringTime();
	}

}
