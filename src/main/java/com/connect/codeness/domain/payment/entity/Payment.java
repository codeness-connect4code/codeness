package com.connect.codeness.domain.payment.entity;

import com.connect.codeness.domain.mentoringschedule.entity.MentoringSchedule;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.entity.CreateTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Payment extends CreateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //결제 고유 식별자

	//연관관계 : N:1
	@ManyToOne
	@JoinColumn(name = "mentee_id")
	private User user;//사용자 고유 식별자 (외래키) - 멘티

	//연관관계 : 1:1
	@OneToOne
	@JoinColumn(name = "mentoring_schedule_id")
	private MentoringSchedule mentoringSchedule;// 멘토링 스케쥴 고유 식별자 (외래키)

	@Column(nullable = true)
	private String pgTid; //PG사 발급 거래 고유 ID

	@Column(nullable = true)
	private String impUid; // 거래 요청시 발급되는 포트원 고유 결제 ID
 
	@Column(nullable = false)
	private BigDecimal paymentCost; //결제 금액

	@Column(nullable = false)
	private String paymentCard; //결제 카드 정보

	@Column(nullable = true)
	private LocalDateTime canceledAt; //결제 취소일

	public Payment() {

	}

	@Builder
	public Payment(User user, MentoringSchedule mentoringSchedule, String pgTid, String impUid, BigDecimal paymentCost,
		String paymentCard, LocalDateTime canceledAt) {
		this.user = user;
		this.mentoringSchedule = mentoringSchedule;
		this.pgTid = pgTid;
		this.impUid = impUid;
		this.paymentCost = paymentCost;
		this.paymentCard = paymentCard;
		this.canceledAt = canceledAt;
	}

	/**
	 * Payment 테이블에 impUid & pgTid 업데이트
	 * ImpUid : 결제 요청시 발급
	 * PgTid : 결제 성공시 발급
 	 */
	public void updateImpUidAndPgTid(String impUid, String pgTid) {
		this.impUid = impUid;
		this.pgTid = pgTid;
	}

	/**
	 * 결제 환불시 취소일 업데이트
	 */
	public void updatePaymentCanceledAt() {
		this.canceledAt = LocalDateTime.now();
	}
}
