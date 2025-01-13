package com.connect.codeness.global.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

	SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	private final long expirationTime = 86400000L; // 24시간

	// JWT 토큰 생성
	public String generateToken(String email, Long userId, String role) {
		return Jwts.builder()
			.setSubject(email)
			.claim("userId", userId)
			.claim("role", role)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	// JWT 토큰에서 클레임 추출
	public Claims extractClaims(String token) {
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody();
	}

	// JWT 토큰에서 사용자 ID 추출
	public Long extractUserId(String token) {

		if (token.startsWith("Bearer ")) {
			token = token.substring(7).trim();
		}

		return extractClaims(token).get("userId", Long.class);
	}
	// JWT 토큰에서 이메일 추출
	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}

	// JWT 토큰 만료 여부 체크
	public boolean isTokenExpired(String token) {
		return extractClaims(token).getExpiration().before(new Date());
	}

	// JWT 토큰 검증
	public boolean validateToken(String token, String email) {
		String extractedEmail = extractEmail(token);
		return (extractedEmail.equals(email) && !isTokenExpired(token));
	}
}
