package com.connect.codeness.domain.payment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

	@GetMapping("/payment")
	public String paymentPage() {
		return "paymentTest";
	}
}
