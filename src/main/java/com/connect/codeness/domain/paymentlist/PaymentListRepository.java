package com.connect.codeness.domain.paymentlist;


import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentListRepository extends JpaRepository<PaymentList, Long> {

	default PaymentList findByIdOrElseThrow(Long PaymentListId){
			return findById(PaymentListId).orElseThrow(()-> new BusinessException(ExceptionType.NOT_FOUND_PAYMENTLIST));
		}

}
