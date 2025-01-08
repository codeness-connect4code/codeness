package com.connect.codeness.domain.paymentlist;


import com.connect.codeness.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentListRepository extends JpaRepository<PaymentList, Long> {

}
