package com.connect.codeness.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.connect.codeness.domain.payment.dto.PaymentRequestDto;
import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class PaymentServiceImplTest {

	@Autowired
	private PaymentService paymentService;

	private Long menteeId;
	private Long mentoringScheduleId;

	//초기 설정
	@BeforeEach
	void setUp() {
		//Given
		menteeId = 1L;
		mentoringScheduleId = 1L;

		//레디스 초기화
	}

	@Test
	@DisplayName("결제 생성 동시성 제어 - 중복 예약 방지 테스트")
	void OnlyOneCreatePaymentForSameMentoringSchedule() throws InterruptedException {
		//Given - 동시 요청을 위한 스레드 준비
		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);//모든 스레드 종료까지 대기
		
		AtomicInteger successCount = new AtomicInteger(0);//성공한 결제 개수
		AtomicInteger failureCount = new AtomicInteger(0);//실패한 결제 개수

		//When - 여러 스레드가 동시에 결제 생성을 시도
		handleConcurrentPaymentCreation(executorService, latch, threadCount, successCount, failureCount);

		//Then - 하나의 결제 생성만 성공했는지 검증
		verifyCreatePaymentResults(threadCount, successCount, failureCount);
	}

	/**
	 * 동시 결제 생성 요청 실행 메서드 - 멀티 스레드
	 */
	private void handleConcurrentPaymentCreation(ExecutorService executorService, CountDownLatch latch, int threadCount,
		AtomicInteger successCount, AtomicInteger failureCount) throws InterruptedException {
		//스레드 개수만큼 반복
		IntStream.range(0, threadCount).forEach(index -> executorService.submit(() -> {
			PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
				.mentoringScheduleId(mentoringScheduleId)
				.paymentCost(BigDecimal.valueOf(1))
				.paymentCard("신용카드")
				.build();
			try {
				log.info("[Thread " + index + "] 결제 시도 중 -----------");
				if (paymentService.createPayment(menteeId, paymentRequestDto) != null) {
					successCount.incrementAndGet(); //성공 스레드 개수 증가
					log.info("✨ [Thread " + index + "] 결제 성공 -----------");
				}
			} catch (Exception e) {
				failureCount.incrementAndGet(); //실패 스레드 개수 증가
				log.error("⛔ [Thread " + index + "] 결제 실패: " + e.getMessage());
			} finally {
				latch.countDown(); // 스레드 종료되면 감소
			}
		}));

		//모든 스레드 종료까지 대기
		latch.await();
		shutdownExecutor(executorService);
	}

	/**
	 * ExecutorService 종료 메서드
	 */
	private void shutdownExecutor(ExecutorService executorService) throws InterruptedException {
		executorService.shutdown();
		if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
			executorService.shutdownNow();
		}
	}

	/**
	 * 결제 생성 테스트 결과 검증 메서드
	 */
	private void verifyCreatePaymentResults(int threadCount, AtomicInteger successCount, AtomicInteger failureCount) {
		//하나의 결제만 성공되었는지 검증
		assertThat(successCount.get()).isEqualTo(1);

		//나머지 스레드는 결제 생성 요청 실패했는지 검증
		assertThat(failureCount.get()).isEqualTo(threadCount - 1);
		
		log.info("🎉 총 결제 성공: " + successCount.get());
		log.error("⛔ 총 결제 실패: " + failureCount.get());
	}
}
