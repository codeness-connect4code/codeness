package com.connect.codeness.domain.payment;


import com.connect.codeness.domain.mentoringschedule.MentoringSchedule;
import com.connect.codeness.domain.mentoringschedule.MentoringScheduleRepository;
import com.connect.codeness.domain.payment.dto.PaymentDeleteRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRequestDto;
import com.connect.codeness.domain.paymentlist.PaymentList;
import com.connect.codeness.domain.paymentlist.PaymentListRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.PaymentStatus;
import com.connect.codeness.global.enums.ReviewStatus;
import com.connect.codeness.global.enums.SettleStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	private final IamportClient iamportClient;
	private final PaymentRepository paymentRepository;
	private final PaymentListRepository paymentListRepository;
	private final UserRepository userRepository;
	private final MentoringScheduleRepository mentoringScheduleRepository;

	public PaymentServiceImpl(IamportClient iamportClient, PaymentRepository paymentRepository,
		PaymentListRepository paymentListRepository,
		UserRepository userRepository, MentoringScheduleRepository mentoringScheduleRepository) {
		this.iamportClient = iamportClient;
		this.paymentRepository = paymentRepository;
		this.paymentListRepository = paymentListRepository;
		this.userRepository = userRepository;
		this.mentoringScheduleRepository = mentoringScheduleRepository;
	}

	/**
	 * 결제 생성 서비스 메서드
	 * - 멘토링 스케쥴 신청
	 */
	@Transactional
	@Override
	public CommonResponseDto createPayment(Long userId, PaymentRequestDto requestDto) {
		//ImpUid 존재하는지 체크
		if (requestDto.getImpUid() != null && paymentRepository.existsByImpUid(requestDto.getImpUid())) {
			throw new BusinessException(ExceptionType.DUPLICATE_VALUE);
		}

		User user = userRepository.findByIdOrElseThrow(userId);
		//멘토링 스케쥴 조회
		MentoringSchedule mentoringSchedule = mentoringScheduleRepository.findByIdOrElseThrow(requestDto.getMentoringScheduleId());

		Payment payment = Payment.builder()
			.user(user)
			.mentoringSchedule(mentoringSchedule)
			.paymentCost(requestDto.getPaymentCost())
			.paymentCard(requestDto.getPaymentCard())
			.build();

		//결제(멘토링 신청) db 저장
		paymentRepository.save(payment);

		return CommonResponseDto.builder().msg("멘토링 스케쥴이 신청 되었습니다.").data(payment.getId()).build();
	}

	/**
	 * 결제 삭제 메서드
	 * - 결제 도중 취소하거나 결제가 거절됐을 경우 결제 데이터 삭제
	 */
	@Transactional
	@Override
	public CommonResponseDto deletePayment(Long paymentId, PaymentDeleteRequestDto requestDto) {
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
	public CommonResponseDto verifyPayment(Long paymentId, PaymentRequestDto requestDto) {
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
		} catch (Exception e) {
			//TODO : API KEY 잘못 되었을 경우, 환불 API 호출 - 환불 API 구현 후 추가 예정
			paymentRepository.deleteById(payment.getId());
			throw new BusinessException(ExceptionType.NOT_FOUND_PAYMENT_BY_IAMPORT);
		}

		//결제 상태 확인
		if (iamportResponse.getResponse() == null || !"paid".equals(
			iamportResponse.getResponse().getStatus())) {
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

		//결제 내역 생성 & 저장
		PaymentList paymentList = PaymentList.builder()
			.payment(payment)
			.user(payment.getUser())
			.pgTid(requestDto.getPgTid())
			.paymentCost(payment.getPaymentCost())
			.paymentCard(payment.getPaymentCard())
			.paymentStatus(PaymentStatus.COMPLETE)
			.settleStatus(SettleStatus.UNPROCESSED)
			.reviewStatus(ReviewStatus.NOT_YET)
			.account(payment.getUser().getAccount())
			.bankName(payment.getUser().getBankName())
			.build();

		//결제 내역 db 저장
		paymentListRepository.save(paymentList);

		return CommonResponseDto.builder().msg("결제가 완료되었습니다.").data(payment.getId()).build();
	}

}

