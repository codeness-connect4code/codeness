package com.connect.codeness.domain.payment.repository;


import com.connect.codeness.domain.payment.entity.Payment;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	default Payment findByIdOrElseThrow(Long PaymentId){
		return findById(PaymentId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT));
	}

	boolean existsByImpUid(String impUid);

	default Payment findByMentoringScheduleIdOrElseThrow(Long mentoringScheduleId) {
		return findById(mentoringScheduleId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT));
	};

	List<Payment> findAllByUserId(Long userId);

	Optional<Payment> findByUserId(Long userId);

	default Payment findByUserIdOrElseThrow(Long userId){
		return findByUserId(userId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENT));
	}
}
