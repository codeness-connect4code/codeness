package com.connect.codeness.domain.settlement.service;


import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.domain.paymenthistory.repository.PaymentHistoryRepository;
import com.connect.codeness.domain.settlement.entity.Settlement;
import com.connect.codeness.domain.settlement.repository.SettlementRepository;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.SettlementStatus;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SettlementServiceImpl implements SettlementService {

	private final UserRepository userRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final SettlementRepository settlementRepository;

	public SettlementServiceImpl(UserRepository userRepository, PaymentHistoryRepository paymentHistoryRepository,
		SettlementRepository settlementRepository) {
		this.userRepository = userRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
		this.settlementRepository = settlementRepository;
	}

	/**
	 * 결제내역 정산 신청 서비스 메서드
	 * - 멘토
	 * - 정산상태 변경 & 유저 계좌, 은행명 & 정산 요청일 저장
	 */
	@Transactional
	@Override
	public CommonResponseDto requestSettlement(Long userId) {
		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		//유저가 멘티거나 어드민일 경우
		if (user.getRole().equals(UserRole.MENTEE) || user.getRole().equals(UserRole.ADMIN)) {
			throw new BusinessException(ExceptionType.FORBIDDEN_SETTLEMENT_ACCESS);
		}

		//결제 내역 조회, 없을 경우 예외처리
		List<PaymentHistory> paymentHistories = paymentHistoryRepository.findAllByUserId(user.getId());
		if (paymentHistories.isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_PAYMENT_HISTORY);
		}

		//정산 상태가 미처리인 정산 조회
		List<Settlement> unprocessedSettlements = settlementRepository.findAllByUserIdAndSettleStatus(user.getId(),
			SettlementStatus.UNPROCESSED);
		if (unprocessedSettlements.isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_SETTLEMENT_DATE);
		}

		//유저 계좌, 은행명 가져오기
		String account = user.getAccount();
		String bankName = user.getBankName();

		if (account == null && bankName == null) {
			throw new BusinessException(ExceptionType.NOT_FOUND_USER_ACCOUNT_AND_BANK_NAME);
		}

		//정산 상태 처리중으로 업데이트
		unprocessedSettlements.forEach(settlement -> {
			settlement.updateSettlement(
				SettlementStatus.PROCESSING,
				user.getAccount(),
				user.getBankName(),
				LocalDateTime.now());
		});

		return CommonResponseDto.builder().msg("정산 신청이 완료되었습니다.").build();
	}

}
