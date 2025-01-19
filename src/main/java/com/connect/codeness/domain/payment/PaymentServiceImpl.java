package com.connect.codeness.domain.payment;


import com.connect.codeness.domain.chat.ChatServiceImpl;
import com.connect.codeness.domain.chat.dto.ChatRoomCreateRequestDto;
import com.connect.codeness.domain.mentoringschedule.MentoringSchedule;
import com.connect.codeness.domain.mentoringschedule.MentoringScheduleRepository;
import com.connect.codeness.domain.payment.dto.PaymentDeleteRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRefundRequestDto;
import com.connect.codeness.domain.payment.dto.PaymentRequestDto;
import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import com.connect.codeness.domain.paymenthistory.PaymentHistoryRepository;
import com.connect.codeness.domain.settlement.Settlement;
import com.connect.codeness.domain.settlement.SettlementRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.BookedStatus;
import com.connect.codeness.global.enums.PaymentStatus;
import com.connect.codeness.global.enums.ReviewStatus;
import com.connect.codeness.global.enums.SettlementStatus;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import com.google.firebase.database.FirebaseDatabase;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	private final IamportClient iamportClient;
	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final UserRepository userRepository;
	private final MentoringScheduleRepository mentoringScheduleRepository;
	private final ChatServiceImpl chatService;
	private final SettlementRepository settlementRepository;

	public PaymentServiceImpl(IamportClient iamportClient, PaymentRepository paymentRepository,
		PaymentHistoryRepository paymentHistoryRepository,
		UserRepository userRepository, MentoringScheduleRepository mentoringScheduleRepository, ChatServiceImpl chatService,
		FirebaseDatabase firebaseDatabase, SettlementRepository settlementRepository) {
		this.iamportClient = iamportClient;
		this.paymentRepository = paymentRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
		this.userRepository = userRepository;
		this.mentoringScheduleRepository = mentoringScheduleRepository;
		this.chatService = chatService;
		this.settlementRepository = settlementRepository;
	}

	/**
	 * 결제 생성 서비스 메서드
	 * - 멘토링 스케쥴 신청
	 * - TODO : 채팅방 생성 로직 추가 & 멘토는 신청 못하는 로직 추가
	 */
	@Transactional
	@Override
	public CommonResponseDto createPayment(Long userId, PaymentRequestDto requestDto) {
		//ImpUid 존재하면 예외처리 : 중복 주문 안됨
		if (requestDto.getImpUid() != null && paymentRepository.existsByImpUid(requestDto.getImpUid())) {
			throw new BusinessException(ExceptionType.DUPLICATE_VALUE);
		}

		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);
		//멘토는 멘토링 신청이 불가함
		if(user.getRole().equals(UserRole.MENTOR)){
			throw new BusinessException(ExceptionType.MENTOR_PAYMENT_NOT_ALLOWED);
		}

		// TODO : 스케쥴 검증 결제 생성에서 X -> 결제 테스트 후 코드 정리하기
//		//멘토링 스케쥴 상태 체크
//		if (mentoringSchedule.getBookedStatus().equals(BookedStatus.BOOKED)) {
//			throw new BusinessException(ExceptionType.ALREADY_BOOKED);
//		}
//
//		//현재 날짜, 시간 체크
//		LocalDateTime now = LocalDateTime.now();
//		LocalDateTime mentoringDateTime = LocalDateTime.of(mentoringSchedule.getMentoringDate(), mentoringSchedule.getMentoringTime());
//		//멘토링 스케쥴이 현재보다 이전이면 예외
//		if (mentoringDateTime.isBefore(now)) {
//			throw new BusinessException(ExceptionType.MENTORING_SCHEDULE_EXPIRED);
//		}

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
		ChatRoomCreateRequestDto chatRoomCreateRequestDto = ChatRoomCreateRequestDto.builder()
			.partnerId(mentor.getId())
			.build();

		//채팅방 생성 - 로그인한 id, 멘토의 id
		chatService.createChatRoom(payment.getUser().getId(), chatRoomCreateRequestDto);

		return CommonResponseDto.builder().msg("결제가 완료되었습니다.").data(payment.getId()).build();
	}

	/**
	 * 결제 환불 서비스 메서드
	 * - 결제 완료 후 환불 진행 : 결제 내역 테이블에서 조회 및 진행
	 */
	@Transactional
	@Override
	public CommonResponseDto refundPayment(Long userId, Long paymentId, PaymentRefundRequestDto requestDto) {

		//결제 내역 테이블 조회 - 로그인한 유저 ID & 결제 ID
		PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserIdOrElseThrow(userId, paymentId);

		//pgTid 유효성 검사
		if (paymentHistory.getPgTid() == null || paymentHistory.getPgTid().isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_PGTID);
		}

		//상태가 결제 취소일 경우
		if (paymentHistory.getPaymentStatus().equals("CANCEL")) {
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
		Payment payment = paymentHistory.getPayment();
		payment.updatePaymentCanceledAt();

		//채팅방 삭제 - 채팅방 id는 로그인한 유저 id랑 상대 멘토 id 조합 -> ex) 1_2
		String chatRoomId = chatService.generateChatRoomId(userId, payment.getUser().getId());
		chatService.deleteChatRoom(userId, chatRoomId);

		return CommonResponseDto.builder().msg("결제가 환불되었습니다.").build();
	}

}

