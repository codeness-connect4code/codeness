package com.connect.codeness.domain.paymentlist;


import com.connect.codeness.domain.payment.Payment;
import com.connect.codeness.domain.payment.PaymentRepository;
import com.connect.codeness.domain.paymentlist.dto.PaymentListResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentListServiceImpl implements PaymentListService {

	private final UserRepository userRepository;
	private final PaymentListRepository paymentListRepository;
	private final PaymentRepository paymentRepository;

	public PaymentListServiceImpl(UserRepository userRepository, PaymentListRepository paymentListRepository,
		PaymentRepository paymentRepository) {
		this.userRepository = userRepository;
		this.paymentListRepository = paymentListRepository;
		this.paymentRepository = paymentRepository;
	}

	/**
	 * 결제내역 전체 조회 서비스 메서드
	 * - 멘티 & 멘토
	 */
	@Override
	public CommonResponseDto<List<PaymentListResponseDto>> getAllPaymentHistory(Long userId) {

		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		//로그인한 유저 id로 결제 list 조회
		List<Payment> payments = paymentRepository.findAllByUserId(user.getId());

		//결제 id로 paymentList 조회, list로 가져오기
		List<PaymentList> paymentLists = payments.stream()
			.flatMap(payment -> paymentListRepository.findAllByPaymentId(payment.getId()).stream())
			.toList();

		List<PaymentListResponseDto> paymentListResponseDtos = paymentLists.stream()
			.map(paymentList -> helperPaymentList(paymentList, user.getRole()))
			.toList();

		return CommonResponseDto.<List<PaymentListResponseDto>>builder()
			.msg("결제 내역이 전체 조회 되었습니다.").data(paymentListResponseDtos).build();
	}

	/**
	 * 유저 롤에 따른 결제 내역 응답 메서드
	 * - TODO : 현재 해당하지 않는 필드는 null로 나오는데 responseDto 멘토, 멘티 분리하는게 나을지 고민
	 */
	private PaymentListResponseDto helperPaymentList(PaymentList paymentList, UserRole userRole) {
		PaymentListResponseDto.PaymentListResponseDtoBuilder builder = PaymentListResponseDto.builder()
			.id(paymentList.getId())
			.paymentId(paymentList.getPayment().getId())
			.mentorId(paymentList.getUser().getId())
			.pgTid(paymentList.getPgTid())
			.paymentCost(paymentList.getPaymentCost())
			.paymentCard(paymentList.getPaymentCard())
			.canceledAt(paymentList.getCanceledAt())
			.reviewStatus(paymentList.getReviewStatus())
			.account(paymentList.getAccount())
			.bankName(paymentList.getBankName());

		//유저가 멘티일 경우
		if (userRole == UserRole.MENTEE) {
			//결제상태 paymentStatus 필드 추가
			builder.paymentStatus(paymentList.getPaymentStatus());
		}
		//유저가 멘토일 경우
		if (userRole == UserRole.MENTOR) {
			//정산상태 settleStatus 필드 추가
			builder.settleStatus(paymentList.getSettleStatus());
		}

		return builder.build();
	}

	/**
	 * 결제내역 단건 조회 서비스 메서드
	 * - 멘티
	 */
	@Override
	public CommonResponseDto<PaymentListResponseDto> getPaymentHistory(Long userId, Long paymentListId) {

		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		//결제 내역 조회
		PaymentList paymentList = paymentListRepository.findByIdAndUserId(paymentListId, user.getId())
			.orElseThrow(() -> new BusinessException(ExceptionType.FORBIDDEN_PAYMENT_ACCESS));

		PaymentListResponseDto paymentListResponseDto = PaymentListResponseDto.builder()
			.id(paymentList.getId())
			.paymentId(paymentList.getPayment().getId())
			.mentorId(paymentList.getUser().getId())
			.pgTid(paymentList.getPgTid())
			.paymentCost(paymentList.getPaymentCost())
			.paymentCard(paymentList.getPaymentCard())
			.paymentStatus(paymentList.getPaymentStatus())
			.canceledAt(paymentList.getCanceledAt())
			.reviewStatus(paymentList.getReviewStatus())
			.account(paymentList.getAccount())
			.bankName(paymentList.getBankName())
			.build();

		return CommonResponseDto.<PaymentListResponseDto>builder().msg("결제 내역이 단건 조회 되었습니다.").data(paymentListResponseDto).build();
	}

	/**
	 * 결제내역 정산 신청 서비스 메서드
	 * - 멘토
	 * - 정산상태 변경
	 */
	@Transactional
	@Override
	public CommonResponseDto requestSettlement(Long userId, Long paymentListId) {
		//유저 조회

		//

		return CommonResponseDto.builder().msg("정산 신청이 완료되었습니다.").build();
	}
}

