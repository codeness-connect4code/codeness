package com.connect.codeness.global.config;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	public PasswordEncoder() {
		this.bCryptPasswordEncoder = new BCryptPasswordEncoder(); // BCryptPasswordEncoder 생성
	}


	// 암호화
	public String encode(String rawPassword) {
		// 비밀번호 암호화
		String encodedPassword
			= BCrypt.withDefaults()
			.hashToString(BCrypt.MIN_COST, rawPassword.toCharArray());
		// {bcrypt} 접두사 수동 추가
		return "{bcrypt}" + encodedPassword;
	}

	// 검증
	public boolean matches(String rawPassword, String encodedPassword) {
		if (encodedPassword.startsWith("{bcrypt}")) {
			// 접두사 제거 후 검증
			encodedPassword = encodedPassword.substring(8);
		}
		BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
		return result.verified; // 일치하면 true

	}
}
