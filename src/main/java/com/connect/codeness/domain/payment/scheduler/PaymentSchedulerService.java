package com.connect.codeness.domain.payment.scheduler;

import com.connect.codeness.domain.payment.entity.Payment;
import com.connect.codeness.domain.payment.repository.PaymentRepository;
import com.connect.codeness.global.enums.BookedStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentSchedulerService {

	private final PaymentRepository paymentRepository;

	public PaymentSchedulerService(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	/**
	 * 결제 데이터 삭제 & 멘토링 스케줄 상태 변경
	 * - 결제 생성 메서드 실행 후 일정 시간 이후 결제가 진행되지 않으면 삭제 & 업데이트
	 */
	@Transactional
	public void deleteUnpaidPaymentAndResetScheduleStatus(Long paymentId) {
		//결제 데이터 조회
		Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);

		//impUid가 null이 아니거나, 비어있지 않은 경우 즉시 반환 (결제 완료)
		if (payment.getImpUid() != null && !payment.getImpUid().isEmpty()) {
			log.info("해당 결제 id '{}'는 결제가 완료되었습니다.", paymentId);
			return;
		}

		//멘토링 스케줄 조회 & 상태 변경
		payment.getMentoringSchedule().updateBookedStatus(BookedStatus.EMPTY);

		//결제 데이터 삭제
		paymentRepository.delete(payment);
		log.info("해당 결제 id '{}'는 삭제가 완료되었습니다.", paymentId);
	}

}
