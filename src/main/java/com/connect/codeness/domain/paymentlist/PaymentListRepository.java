package com.connect.codeness.domain.paymentlist;


import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentListRepository extends JpaRepository<PaymentList, Long> {

	default PaymentList findByIdOrElseThrow(Long PaymentListId) {
		return findById(PaymentListId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENTLIST));
	}

	Optional<PaymentList> findByPaymentId(Long paymentId);

	default PaymentList findByPaymentIdOrElseThrow(Long paymentId) {
		return findByPaymentId(paymentId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT));
	}

	List<PaymentList> findAllByPaymentId(Long paymentId);

	@Query("SELECT p FROM PaymentList p WHERE p.id = :paymentListId AND p.payment.user.id = :userId")
	Optional<PaymentList> findByIdAndUserId(Long paymentListId, Long userId);

}
