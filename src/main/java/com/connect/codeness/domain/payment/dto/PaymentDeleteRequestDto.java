package com.connect.codeness.domain.payment.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentDeleteRequestDto {

	@NotNull
	private final String impUid; //거래 요청시 발급되는 포트원 고유 결제 ID

}
