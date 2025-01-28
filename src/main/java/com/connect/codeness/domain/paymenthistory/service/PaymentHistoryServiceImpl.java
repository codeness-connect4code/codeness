package com.connect.codeness.domain.paymenthistory.service;


import com.connect.codeness.domain.payment.entity.Payment;
import com.connect.codeness.domain.payment.repository.PaymentRepository;
import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.domain.paymenthistory.repository.PaymentHistoryRepository;
import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryMenteeResponseDto;
import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryMentorResponseDto;
import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryResponseDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

	private final UserRepository userRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final PaymentRepository paymentRepository;

	public PaymentHistoryServiceImpl(UserRepository userRepository, PaymentHistoryRepository paymentHistoryRepository,
		PaymentRepository paymentRepository) {
		this.userRepository = userRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
		this.paymentRepository = paymentRepository;
	}

	/**
	 * 결제내역 전체 조회 서비스 메서드
	 * - 멘티 & 멘토
	 */
	@Override
	public CommonResponseDto<?> getAllPaymentHistory(Long userId) {

		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		if(user.getRole().equals(UserRole.MENTEE)){

			//로그인한 유저 id로 결제 list를 조회
			List<Payment> payments = paymentRepository.findAllByUserId(user.getId());

			//결제 list에 대한 결제내역 list를 조회
			List<PaymentHistory> paymentHistories = payments.stream()
				.flatMap(payment -> paymentHistoryRepository.findAllByPaymentId(payment.getId()).stream())
				.toList();

			List<PaymentHistoryMenteeResponseDto> paymentHistoryMenteeResponseDtos = paymentHistories.stream()
				.map(PaymentHistoryMenteeResponseDto::from)
				.toList();
			return CommonResponseDto.<List<PaymentHistoryMenteeResponseDto>>builder()
				.msg("멘티 결제 내역이 전체 조회 되었습니다.").data(paymentHistoryMenteeResponseDtos).build();
		}
		//멘토의 경우
		if(user.getRole().equals(UserRole.MENTOR)){
			//로그인한 userId로 결제내역 list를 조회
			List<PaymentHistory> paymentHistoryList =  paymentHistoryRepository.findAllByUserId(userId);

			List<PaymentHistoryMentorResponseDto> paymentHistoryMentorResponseDtos = paymentHistoryList.stream()
				.map(PaymentHistoryMentorResponseDto::from)
				.toList();
			return CommonResponseDto.<List<PaymentHistoryMentorResponseDto>>builder()
				.msg("멘토 결제 내역이 전체 조회 되었습니다.").data(paymentHistoryMentorResponseDtos).build();
		} else {
			throw new BusinessException(ExceptionType.NOT_FOUND_USER);//TODO : 유저 롤 예외처리 추가
		}
	}

	/**
	 * 결제내역 단건 상세 조회 서비스 메서드
	 * - 멘티 & 멘토
	 * - TODO : 단일 쿼리로 최적화 고민하기
	 */
	@Override
	public CommonResponseDto<PaymentHistoryResponseDto> getPaymentHistory(Long userId, Long paymentHistoryId) {

		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		//로그인한 유저에 해당하는 결제 List 조회
		List<Payment> payments = paymentRepository.findAllByUserId(user.getId());

		//결제 데이터가 없을 경우 예외처리
		if(payments.isEmpty()){
			throw new BusinessException(ExceptionType.NOT_FOUND_PAYMENT);
		}

		//조회된 결제 List와 결제 내역 id가 일치하는 결제 내역 조회 - 다른 유저 결제내역 조회시 예외
		PaymentHistory paymentHistory = payments.stream()
			.map(payment -> paymentHistoryRepository.findByIdAndPaymentId(paymentHistoryId, payment.getId()))
			.flatMap(Optional::stream)//Optional 값이 존재하면 stream에 포함, 없으면 무시
			.findFirst()//첫번째 값
			.orElseThrow(() -> new BusinessException(ExceptionType.FORBIDDEN_PAYMENT_HISTORY_ACCESS));

		//dto 생성
		PaymentHistoryResponseDto paymentHistoryResponseDto = PaymentHistoryResponseDto.from(paymentHistory);

		return CommonResponseDto.<PaymentHistoryResponseDto>builder().msg("결제 내역이 단건 조회 되었습니다.").data(paymentHistoryResponseDto).build();
	}

}

