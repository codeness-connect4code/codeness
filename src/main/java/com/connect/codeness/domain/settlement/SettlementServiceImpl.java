package com.connect.codeness.domain.settlement;


import com.connect.codeness.domain.paymenthistory.PaymentHistoryRepository;
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
	 * - 정산상태 변경
	 * - TODO : 결제내역 데이터가 없을 경우 예외처리 추가 & 정산 신청일 업데이트
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

		//정산 상태가 미처리인 정산 조회
		List<Settlement> unprocessedSettlements = settlementRepository.findAllByUserIdAndSettleStatus(user.getId(), SettlementStatus.UNPROCESSED);
		if(unprocessedSettlements.isEmpty()){
			throw new BusinessException(ExceptionType.NOT_FOUND_SETTLEMENT_DATE);
		}

		//정산 상태 처리중으로 업데이트
		unprocessedSettlements.forEach(settlement -> settlement.updateSettlementStatus(SettlementStatus.PROCESSING));

		return CommonResponseDto.builder().msg("정산 신청이 완료되었습니다.").build();
	}

}
