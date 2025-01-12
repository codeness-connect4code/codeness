package com.connect.codeness.domain.payment.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentDeleteRequestDto {

	@NotNull
	private String impUid;//포트원 결제 요청시 생성되는 기본키

}
