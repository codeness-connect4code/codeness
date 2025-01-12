package com.connect.codeness.domain.payment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

	@GetMapping("/login-page")
	public String loginPage() {
		return "loginPage";
	}
}
