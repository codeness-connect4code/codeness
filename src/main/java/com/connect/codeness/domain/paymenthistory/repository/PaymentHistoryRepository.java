package com.connect.codeness.domain.paymenthistory.repository;


import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

	default PaymentHistory findByIdOrElseThrow(Long paymentHistoryId) {
		return findById(paymentHistoryId).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT_HISTORY));
	}

	Optional<PaymentHistory> findByPaymentId(Long paymentId);

	default PaymentHistory findByPaymentIdOrElseThrow(Long paymentId) {
		return findByPaymentId(paymentId).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT));
	}

	List<PaymentHistory> findAllByPaymentId(Long paymentId);

	@Query("SELECT p FROM PaymentHistory p WHERE p.id = :paymentHistoryId AND p.user.id = :userId")
	Optional<PaymentHistory> findByIdAndUserId(Long paymentHistoryId, Long userId);

	default PaymentHistory findByIdAndUserIdOrElseThrow(Long userId, Long paymentHistoryId){
		return findByIdAndUserId(userId, paymentHistoryId).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT_HISTORY));
	}

	@Query("""
    SELECT ph FROM PaymentHistory ph
    JOIN ph.payment p
    WHERE ph.id = :paymentHistoryId
      AND p.id = :paymentId
    """)
	Optional<PaymentHistory> findByIdAndPaymentId(Long paymentHistoryId, Long paymentId);

	List<PaymentHistory> findAllByUserId(Long userId);

}

