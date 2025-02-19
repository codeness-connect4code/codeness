package com.connect.codeness.domain.paymenthistory.entity;


import com.connect.codeness.domain.payment.entity.Payment;
import com.connect.codeness.domain.settlement.entity.Settlement;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.entity.CreateTimeEntity;
import com.connect.codeness.global.enums.PaymentStatus;
import com.connect.codeness.global.enums.ReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "payment_history")
public class PaymentHistory extends CreateTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;//결제 내역 고유 식별자

	//연관관계 : N:1
	@OneToOne(fetch = FetchType.LAZY)//필요한 시점에만 데이터 로드
	@JoinColumn(name = "payment_id")
	private Payment payment;//결제 고유 식별자 (외래키)

	//연관관계 : N:1
	@ManyToOne(fetch = FetchType.LAZY)//필요한 시점에만 데이터 로드
	@JoinColumn(name = "mentor_id")
	private User user;//사용자 고유 식별자 (외래키) - 멘토

	@Column(nullable = false)
	private String pgTid; //PG사 발급 거래 ID

	@Column(nullable = false)
	private BigDecimal paymentCost; //결제 금액

	@Column(nullable = false)
	private String paymentCard; //결제 카드 정보

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus;//결제 상태

	@Column(nullable = true)
	private LocalDateTime canceledAt; //결제 취소일

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReviewStatus reviewStatus; //후기 작성 상태

	@OneToOne(mappedBy = "paymentHistory", fetch = FetchType.LAZY)
	private Settlement settlement; //양방향 매핑

	public PaymentHistory() {

	}

	@Builder
	public PaymentHistory(Payment payment, User user, String pgTid, BigDecimal paymentCost, String paymentCard, PaymentStatus paymentStatus,
		LocalDateTime canceledAt, ReviewStatus reviewStatus) {
		this.payment = payment;
		this.user = user;
		this.pgTid = pgTid;
		this.paymentCost = paymentCost;
		this.paymentCard = paymentCard;
		this.paymentStatus = paymentStatus;
		this.canceledAt = canceledAt;
		this.reviewStatus = reviewStatus;
	}

	/**
	 * Review 상태 업데이트 메서드
	 */
	public void updateReviewStatus(ReviewStatus status) {
		this.reviewStatus = status;
	}

	/**
	 * paymentList 결제 상태 수정 COMPLETE -> CANCEL
	 */
	public void updatePaymentStatus(PaymentStatus paymentStatus, LocalDateTime canceledAt) {
		this.paymentStatus = paymentStatus;
		this.canceledAt = LocalDateTime.now();
	}

}
