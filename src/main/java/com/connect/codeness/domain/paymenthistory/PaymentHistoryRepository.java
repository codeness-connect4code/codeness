package com.connect.codeness.domain.paymenthistory;


import com.connect.codeness.domain.admin.dto.AdminSettlementListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminSettlementResponseDto;
import com.connect.codeness.global.enums.SettlementStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

	default PaymentHistory findByIdOrElseThrow(Long paymentHistoryId) {
		return findById(paymentHistoryId).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENTLIST));
	}

	Optional<PaymentHistory> findByPaymentId(Long paymentId);

	default PaymentHistory findByPaymentIdOrElseThrow(Long paymentId) {
		return findByPaymentId(paymentId).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT));
	}

	List<PaymentHistory> findAllByPaymentId(Long paymentId);

	@Query("SELECT p FROM PaymentHistory p WHERE p.id = :paymentHistoryId AND p.payment.user.id = :userId")
	Optional<PaymentHistory> findByIdAndUserId(Long paymentHistoryId, Long userId);

	/**
	 * TODO : 사용 안하면 지우기
	 */
	Optional<PaymentHistory> findByUserId(Long userId);

	List<PaymentHistory> findAllByUserIdAndSettleStatus(Long userId, SettlementStatus settleStatus);
}

