package com.connect.codeness.domain.payment.scheduler;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentScheduler {

	private final ThreadPoolTaskScheduler paymentTaskScheduler;
	private final PaymentSchedulerService paymentSchedulerService;

	public PaymentScheduler(ThreadPoolTaskScheduler paymentTaskScheduler, PaymentSchedulerService paymentSchedulerService) {
		this.paymentTaskScheduler = paymentTaskScheduler;
		this.paymentSchedulerService = paymentSchedulerService;
	}

	/**
	 * 결제 데이터 삭제 & 멘토링 스케줄 상태 변경 스케줄링
	 * - 20분 후 진행
	 * - 트랜잭션 사용으로 메서드 분리
	 */
	public void schedulePaymentDeletion(Long paymentId) {
		//20분 후 실행
		Instant executionTime = Instant.now().plusSeconds(20 * 60);
		
		paymentTaskScheduler.schedule(() -> {
			log.info("결제 데이터 삭제 스케줄러 실행 : 결제 id '{}'", paymentId);
			paymentSchedulerService.deleteUnpaidPaymentAndResetScheduleStatus(paymentId);
		}, executionTime);
	}

}
