package com.connect.codeness.domain.payment;

import com.connect.codeness.domain.mentoringschedule.MentoringSchedule;
import com.connect.codeness.domain.user.User;
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
	private User user;//사용자 고유 식별자 (외래키)

	//연관관계 : 1:1
	@OneToOne
	@JoinColumn(name = "mentoring_schedule_id")
	private MentoringSchedule mentoringSchedule;// 멘토링 스케쥴 고유 식별자 (외래키)

	@Column(nullable = false)
	private String pgTid; //PG사 발급 거래 ID

	@Column(nullable = false)
	private BigDecimal paymentCost; //결제 금액

	@Column(nullable = false)
	private String paymentCard; //결제 카드 정보

	@Column(nullable = true)
	private LocalDateTime canceledAt; //결제 취소일

}
