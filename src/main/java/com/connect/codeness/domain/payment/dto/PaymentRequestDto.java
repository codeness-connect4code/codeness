package com.connect.codeness.domain.payment.dto;


import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentRequestDto {

	@NotNull
	private final Long mentoringScheduleId; //멘토링 스케쥴

	private final String pgTid; //PG사 발급 거래 고유 ID
	
	private final String impUid; //거래 요청시 발급되는 포트원 고유 결제 ID

	@NotNull
	private final BigDecimal paymentCost; //결제 금액

	@NotNull
	private final String paymentCard; //결제 카드 정보

}
