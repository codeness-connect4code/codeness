package com.connect.codeness.domain.paymenthistory;


import com.connect.codeness.domain.payment.Payment;
import com.connect.codeness.domain.payment.PaymentRepository;
import com.connect.codeness.domain.paymenthistory.dto.PaymentHistoryResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.SettlementStatus;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * - TODO : 어드민 추가
	 */
	@Override
	public CommonResponseDto<List<PaymentHistoryResponseDto>> getAllPaymentHistory(Long userId) {

		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		//로그인한 유저 id로 결제 list를 조회
		List<Payment> payments = paymentRepository.findAllByUserId(user.getId());

		//결제 list에 대한 결제내역 list를 조회
		List<PaymentHistory> paymentHistories = payments.stream()
			.flatMap(payment -> paymentHistoryRepository.findAllByPaymentId(payment.getId()).stream())
			.toList();

		//결제내역 list를 dto로 변환
		List<PaymentHistoryResponseDto> paymentHistoryResponseDtos = paymentHistories.stream()
			.map(paymentHistory -> helperPaymentHistory(paymentHistory, user.getRole()))
			.toList();

		return CommonResponseDto.<List<PaymentHistoryResponseDto>>builder()
			.msg("결제 내역이 전체 조회 되었습니다.").data(paymentHistoryResponseDtos).build();
	}

	/**
	 * 유저 롤에 따른 결제 내역 응답 메서드
	 * - TODO : 현재 해당하지 않는 필드는 null로 나오는데 responseDto 멘토, 멘티 분리하는게 나을지 고민
	 * -
	 */
	private PaymentHistoryResponseDto helperPaymentHistory(PaymentHistory paymentHistory, UserRole userRole) {
		PaymentHistoryResponseDto.PaymentHistoryResponseDtoBuilder builder = PaymentHistoryResponseDto.builder()
			.id(paymentHistory.getId())
			.paymentId(paymentHistory.getPayment().getId())
			.mentorId(paymentHistory.getUser().getId())
			.pgTid(paymentHistory.getPgTid())
			.paymentCost(paymentHistory.getPaymentCost())
			.paymentCard(paymentHistory.getPaymentCard())
			.canceledAt(paymentHistory.getCanceledAt())
			.reviewStatus(paymentHistory.getReviewStatus())
			.account(paymentHistory.getAccount())
			.bankName(paymentHistory.getBankName());

		//유저가 멘티일 경우
		if (userRole == UserRole.MENTEE) {
			//결제상태 paymentStatus 필드 추가
			builder.paymentStatus(paymentHistory.getPaymentStatus());
		}
		//유저가 멘토일 경우
		if (userRole == UserRole.MENTOR) {
			//정산상태 settlementStatus 필드 추가
			builder.settlementStatus(paymentHistory.getSettlementStatus());
		}

		return builder.build();
	}

	/**
	 * 결제내역 단건 조회 서비스 메서드
	 * - 멘티 & 멘토
	 * - TODO : 로그인한 회원만 조회 빼기
	 */
	@Override
	public CommonResponseDto<PaymentHistoryResponseDto> getPaymentHistory(Long userId, Long paymentHistoryId) {

		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		//결제 내역 조회 - 다른 유저 결제내역 조회시 예외
		PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(paymentHistoryId, user.getId())
			.orElseThrow(() -> new BusinessException(ExceptionType.FORBIDDEN_PAYMENT_ACCESS));

		PaymentHistoryResponseDto paymentHistoryResponseDto = PaymentHistoryResponseDto.builder()
			.id(paymentHistory.getId())
			.paymentId(paymentHistory.getPayment().getId())
			.mentorId(paymentHistory.getUser().getId())
			.pgTid(paymentHistory.getPgTid())
			.paymentCost(paymentHistory.getPaymentCost())
			.paymentCard(paymentHistory.getPaymentCard())
			.paymentStatus(paymentHistory.getPaymentStatus())
			.canceledAt(paymentHistory.getCanceledAt())
			.reviewStatus(paymentHistory.getReviewStatus())
			.account(paymentHistory.getAccount())
			.bankName(paymentHistory.getBankName())
			.build();

		return CommonResponseDto.<PaymentHistoryResponseDto>builder().msg("결제 내역이 단건 조회 되었습니다.").data(paymentHistoryResponseDto).build();
	}

	/**
	 * 결제내역 정산 신청 서비스 메서드
	 * - 멘토
	 * - 정산상태 변경
	 * - TODO : 결제내역 데이터가 없을 경우 예외처리 추가
	 */
	@Transactional
	@Override
	public CommonResponseDto requestSettlement(Long userId) {
		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		//유저가 멘티거나 어드민일 경우
		if(user.getRole().equals(UserRole.MENTEE) || user.getRole().equals(UserRole.ADMIN)){
			throw new BusinessException(ExceptionType.FORBIDDEN_SETTLEMENT_ACCESS);
		}

		//정산 상태가 미처리인 결제 내역 조회
		List<PaymentHistory> unprocessedPaymentHistories = paymentHistoryRepository.findAllByUserIdAndSettlementStatus(user.getId(), SettlementStatus.UNPROCESSED);
		//정산 상태가 미처리인 결제 내역이 없을 경우
		if(unprocessedPaymentHistories.isEmpty()){
			throw new BusinessException(ExceptionType.NOT_FOUND_SETTLEMENT_DATE);
		}

		//정산 상태 처리중으로 업데이트
		unprocessedPaymentHistories.forEach(paymentHistory -> paymentHistory.updateSettleStatus(
			SettlementStatus.PROCESSING));

		return CommonResponseDto.builder().msg("정산 신청이 완료되었습니다.").build();
	}
}

