package com.connect.codeness.domain.paymentlist.dto;

import com.connect.codeness.domain.payment.Payment;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.enums.PaymentStatus;
import com.connect.codeness.global.enums.ReviewStatus;
import com.connect.codeness.global.enums.SettleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentListResponseDto {

	private Long id;//결제 내역 고유 식별자

	private Long paymentId;//결제 고유 식별자 (외래키)

	private Long mentorId;//사용자 고유 식별자 (외래키)

	private String pgTid; //PG사 발급 거래 ID

	private BigDecimal paymentCost; //결제 금액

	private String paymentCard; //결제 카드 정보

	private PaymentStatus paymentStatus;//결제 상태

	private SettleStatus settleStatus; //정산 상태

	private LocalDateTime canceledAt; //결제 취소일

	private ReviewStatus reviewStatus; //후기 작성 상태

	private String account; //사용자 계좌

	private String bankName; //계좌 은행명

}
