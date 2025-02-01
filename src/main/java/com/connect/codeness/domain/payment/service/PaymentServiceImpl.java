package com.connect.codeness.domain.payment.service;


import com.connect.codeness.domain.mentoringschedule.entity.MentoringSchedule;
import com.connect.codeness.domain.mentoringschedule.repository.MentoringScheduleRepository;
import com.connect.codeness.domain.payment.dto.PaymentResponseDto;
import com.connect.codeness.domain.payment.entity.Payment;
import com.connect.codeness.domain.payment.repository.PaymentRepository;
import com.connect.codeness.domain.payment.dto.PaymentDeleteRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRefundRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRequestDto;
import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.domain.paymenthistory.repository.PaymentHistoryRepository;
import com.connect.codeness.domain.settlement.entity.Settlement;
import com.connect.codeness.domain.settlement.repository.SettlementRepository;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.BookedStatus;
import com.connect.codeness.global.enums.PaymentStatus;
import com.connect.codeness.global.enums.ReviewStatus;
import com.connect.codeness.global.enums.SettlementStatus;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import com.connect.codeness.global.service.RedisLockService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	private final IamportClient iamportClient;
	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final UserRepository userRepository;
	private final MentoringScheduleRepository mentoringScheduleRepository;
	private final SettlementRepository settlementRepository;
	private final RedisLockService redisLockService;
	private final RedissonClient redissonClient;

	public PaymentServiceImpl(IamportClient iamportClient, PaymentRepository paymentRepository,
		PaymentHistoryRepository paymentHistoryRepository, UserRepository userRepository,
		MentoringScheduleRepository mentoringScheduleRepository, SettlementRepository settlementRepository,
		RedisLockService redisLockService, RedissonClient redissonClient) {
		this.iamportClient = iamportClient;
		this.paymentRepository = paymentRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
		this.userRepository = userRepository;
		this.mentoringScheduleRepository = mentoringScheduleRepository;
		this.settlementRepository = settlementRepository;
		this.redisLockService = redisLockService;
		this.redissonClient = redissonClient;
	}


	/**
	 * 결제 생성 서비스 메서드
	 * - 멘토링 스케쥴 신청
	 * - TODO : 동시성 제어 추가
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	@Override
	public CommonResponseDto<?> createPayment(Long userId, PaymentRequestDto requestDto) {

		//락 적용할 Key -> 같은 멘토링 스케쥴에 대한 결제 동시성 제어
		String lockKey = "mentoringSchedule:" + requestDto.getMentoringScheduleId();
		RLock lock = redissonClient.getLock(lockKey);

		//redis 락 획득 (최대 5초 대기, 락 보유 시간 60초)
		boolean isLocked = redisLockService.acquireLock(lockKey, 5000, 60000);
		System.out.println("락 획득 시도: " + lockKey + " -> 결과: " + isLocked);

		if(!isLocked){
			System.out.println("락 획득 실패: " + lockKey);
			throw new BusinessException(ExceptionType.CONCURRENT_PAYMENT_ATTEMPT);
		}

		try{
			System.out.println("락 획득 성공: " + lockKey);

			//유저 조회
			User user = userRepository.findByIdOrElseThrow(userId);
			//멘토는 멘토링 신청이 불가함
			if (user.getRole().equals(UserRole.MENTOR)) {
				throw new BusinessException(ExceptionType.MENTOR_PAYMENT_NOT_ALLOWED);
			}

			//멘토링 스케쥴 조회
			MentoringSchedule mentoringSchedule = mentoringScheduleRepository.findByIdOrElseThrow(requestDto.getMentoringScheduleId());

			//멘토링 스케쥴이 예약 진행중이면 예외 -> 중복 결제 방지
			if(mentoringSchedule.getBookedStatus().equals(BookedStatus.IN_PROGRESS)){
				throw new BusinessException(ExceptionType.DUPLICATE_PAYMENT);
			}

			//멘토링 스케쥴 예약 진행중으로 상태 변경 & 저장
			mentoringSchedule.updateBookedStatus(BookedStatus.IN_PROGRESS);
			mentoringScheduleRepository.save(mentoringSchedule);

			//스케쥴 상태 추가 -> 중복 결제 방지
			Payment payment = Payment.builder()
				.user(user)
				.mentoringSchedule(mentoringSchedule)
				.paymentCost(requestDto.getPaymentCost())
				.paymentCard(requestDto.getPaymentCard())
				.build();

			//결제(멘토링 신청) db 저장
			paymentRepository.save(payment);

			//트랜잭션 완료된 후 락 해제를 실행하는 이벤트 등록
			unlockEvent(lockKey);

			return CommonResponseDto.builder().msg("멘토링 스케쥴이 신청 되었습니다.").data(payment.getId()).build();
		} catch (Exception exception) {
			System.out.println("트랜잭션 롤백 발생: " + exception.getMessage());
			throw exception;
		}
	}

	/**
	 * 트랜잭션이 커밋된 후 락을 해제하는 이벤트 메서드
	 */
	private void unlockEvent(String lockKey) {

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				if (redissonClient.getLock(lockKey).isHeldByCurrentThread()) {
					redissonClient.getLock(lockKey).unlock();
					System.out.println("트랜잭션 완료 후 락 해제: " + lockKey);
				}
			}
		});
	}

	/**
	 * 결제 삭제 서비스 메서드
	 * - 클라이언트에서 결제 도중 취소하거나 결제 창을 빠져나가 결제가 중단 되었을 경우
	 * - paymentId만 가지고 데이터 삭제를 진행 -> 데이터 저장해 둘 필요가 없음
	 * - TODO : 멘토링 상태 변경해줘야함
	 */
	@Transactional
	@Override
	public CommonResponseDto<?> deletePaymentOnCancel(Long paymentId) {
		//결제(멘토링 신청 내역) 확인
		Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);

		//해당 결제 삭제
		paymentRepository.deleteById(paymentId);

		return CommonResponseDto.builder().msg("결제 데이터가 삭제 되었습니다.").build();
	}

	/**
	 * 결제 삭제 서비스 메서드
	 * - 결제 진행시, 잔액 부족 등으로 결제가 거절되었을 경우 결제 데이터 삭제
	 * - TODO : 멘토링 상태 변경해줘야함
	 */
	@Transactional
	@Override
	public CommonResponseDto<?> deletePaymentOnRejection(Long paymentId, PaymentDeleteRequestDto requestDto) {
		//결제(멘토링 신청 내역) 확인
		Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);

		//Iamport api 호출해서 결제 검증
		IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse;
		try {
			iamportResponse = iamportClient.paymentByImpUid(requestDto.getImpUid());
		} catch (Exception e) {
			throw new BusinessException(ExceptionType.NOT_FOUND_IMPUID);
		}

		//결제 상태 확인
		if (iamportResponse.getResponse().getPgTid() == null || !"paid".equals(iamportResponse.getResponse().getStatus())) {
			//결제 실패시 해당 결제 삭제
			paymentRepository.deleteById(paymentId);
		}

		return CommonResponseDto.builder().msg("결제 데이터가 삭제 되었습니다.").build();
	}

	/**
	 * 결제 검증 서비스 메서드
	 */
	@Transactional
	@Override
	public CommonResponseDto<PaymentResponseDto> verifyPayment(Long paymentId, PaymentRequestDto requestDto) {
		//ImpUid 유효성 검사
		if (requestDto.getImpUid() == null || requestDto.getImpUid().isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_IMPUID);
		}
		//PgTid 유효성 검사
		if (requestDto.getPgTid() == null || requestDto.getPgTid().isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_PGTID);
		}

		//결제(멘토링 신청) 데이터 조회
		Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);

		//Iamport api 호출해서 결제 검증
		IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse;
		try {
			iamportResponse = iamportClient.paymentByImpUid(requestDto.getImpUid());
		} catch (IamportResponseException | IOException e) {
			throw new BusinessException(ExceptionType.NOT_FOUND_PAYMENT_BY_IAMPORT);
		}

		//결제 상태 확인
		if (iamportResponse.getResponse() == null || !"paid".equals(iamportResponse.getResponse().getStatus())) {
			//결제 실패시 해당 결제 삭제
			paymentRepository.deleteById(payment.getId());
			throw new BusinessException(ExceptionType.INVALID_PAYMENT);
		}

		//결제 금액 검증
		if (iamportResponse.getResponse().getAmount().compareTo(requestDto.getPaymentCost()) != 0) {
			throw new BusinessException(ExceptionType.NOT_FOUND_AMOUNT);
		}

		//Payment에 ImpUid, PgTid 업데이트
		payment.updateImpUidAndPgTid(requestDto.getImpUid(), requestDto.getPgTid());

		//멘토링 공고 올린 멘토 조회
		User mentor = mentoringScheduleRepository.findMentorById(payment.getMentoringSchedule().getId());

		//결제 내역 생성 & 저장
		PaymentHistory paymentHistory = PaymentHistory.builder()
			.payment(payment)
			.user(mentor)
			.pgTid(requestDto.getPgTid())
			.paymentCost(payment.getPaymentCost())
			.paymentCard(payment.getPaymentCard())
			.paymentStatus(PaymentStatus.COMPLETE)
			.reviewStatus(ReviewStatus.NOT_YET)
			.build();

		//결제 내역 db 저장
		paymentHistoryRepository.save(paymentHistory);

		//멘토링 스케쥴 상태 변경
		MentoringSchedule mentoringSchedule = mentoringScheduleRepository.findByIdOrElseThrow(requestDto.getMentoringScheduleId());
		mentoringSchedule.updateBookedStatus(BookedStatus.BOOKED);

		//정산 생성
		Settlement settlement = Settlement.builder()
			.paymentHistory(paymentHistory)
			.user(mentor)
			.settlementStatus(SettlementStatus.UNPROCESSED)
			.build();

		//정산 저장
		settlementRepository.save(settlement);

		//dto 생성
		PaymentResponseDto paymentResponseDto = PaymentResponseDto.builder()
			.partnerId(mentor.getId())
			.mentoringDate(payment.getMentoringSchedule().getMentoringDate())
			.mentoringTime(payment.getMentoringSchedule().getMentoringTime())
			.build();

		return CommonResponseDto.<PaymentResponseDto>builder().msg("결제가 완료되었습니다.").data(paymentResponseDto).build();
	}

	/**
	 * 결제 환불 서비스 메서드
	 * - 결제 완료 후 환불 진행 : 결제 내역 테이블에서 조회 및 진행
	 * - TODO : 멘토링 스케쥴 시간 검증
	 */

	@Transactional
	@Override
	public CommonResponseDto<PaymentResponseDto> refundPayment(Long userId, Long paymentId, PaymentRefundRequestDto requestDto) {

		//결제 조회 - 로그인한 유저 ID & 결제 ID
		Payment payment = paymentRepository.findByIdAndUserId(userId, paymentId)
			.orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT));

		//현재 날짜 & 시간
		LocalDate currentDate = LocalDate.now();
		LocalTime currentTime = LocalTime.now();

		//멘토링 스케쥴 날짜, 시간 검증
		LocalDate mentoringDate = payment.getMentoringSchedule().getMentoringDate();
		LocalTime mentoringTime = payment.getMentoringSchedule().getMentoringTime();

		//멘토링 스케쥴 날짜가 지금보다 과거이거나, 현재 날짜이지만 시간이 이전이면 환불 불가
		if (mentoringDate.isBefore(currentDate) ||
			(mentoringDate.isEqual(currentDate) && mentoringTime.isBefore(currentTime))) {
			throw new BusinessException(ExceptionType.MENTORING_SCHEDULE_EXPIRED);
		}

 		//pgTid 유효성 검사
		if (payment.getPgTid() == null || payment.getPgTid().isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_PGTID);
		}

		//결제 내역 조회
		PaymentHistory paymentHistory = paymentHistoryRepository.findByPaymentIdOrElseThrow(payment.getId());
		//상태가 결제 취소일 경우
		if (paymentHistory.getPaymentStatus().equals(PaymentStatus.CANCEL)) {
			throw new BusinessException(ExceptionType.ALREADY_CANCEL);
		}

		//Iamport api 호출 - 결제 환불 요청
		IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse;

		String impUid = paymentHistory.getPayment().getImpUid();
		//impUid 유효성 검사
		if (impUid == null || impUid.isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_IMPUID);
		}

		try {
			//imp_uid 전달 - true : imp_uid
			CancelData cancelData = new CancelData(impUid, true);
			iamportResponse = iamportClient.cancelPaymentByImpUid(cancelData);
		} catch (IamportResponseException | IOException e) {
			throw new BusinessException(ExceptionType.REFUND_FAILED);
		}

		//환불 상태 체크
		if (iamportResponse.getResponse() == null || !"cancelled".equals(
			iamportResponse.getResponse().getStatus())) {
			throw new BusinessException(ExceptionType.REFUND_FAILED);
		}

		//멘토링 스케쥴 예약 상태 변경
		MentoringSchedule mentoringSchedule = paymentHistory.getPayment().getMentoringSchedule();
		mentoringSchedule.updateBookedStatus(BookedStatus.EMPTY);

		//결제 내역 업데이트 : 상태, 취소일
		paymentHistory.updatePaymentStatus(PaymentStatus.CANCEL, paymentHistory.getCanceledAt());
		//결제 업데이트 : 취소일
		payment.updatePaymentCanceledAt();

		//dto 생성
		PaymentResponseDto paymentResponseDto = PaymentResponseDto.builder()
			.partnerId(paymentHistory.getUser().getId())
			.mentoringDate(payment.getMentoringSchedule().getMentoringDate())
			.mentoringTime(payment.getMentoringSchedule().getMentoringTime())
			.build();

		return CommonResponseDto.<PaymentResponseDto>builder().msg("결제가 환불되었습니다.").data(paymentResponseDto).build();
	}

}

