package com.connect.codeness.domain.settlement;

import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.enums.SettleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
public class Settlement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //정산 고유 식별자

	@OneToOne
	@JoinColumn(name = "payment_history_id")
	private PaymentHistory paymentHistoryId; //결제 내역 고유 식별자 (외래키)

	@ManyToOne
	@JoinColumn(name = "mentor_id")
	private User user; //멘토 고유 식별자 (외래키)

	@Column(nullable = false)
	private SettleStatus settleStatus; //정산 상태

	@Column(nullable = true)
	private String account; //사용자 계좌

	@Column(nullable = true)
	private String bankName; //정산 고유 식별자

	@Column(nullable = true)
	private LocalDateTime settlementRequestAt; //정산 요청일

	public Settlement(){

	}
}
