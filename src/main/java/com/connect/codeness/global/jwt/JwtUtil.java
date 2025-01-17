package com.connect.codeness.global.jwt;

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
	private final long refreshThreshold = 3600000L; // 1시간 (갱신 필요 여부를 판단하는 임계값)

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
		if (token.startsWith("Bearer ")) {
			token = token.substring(7).trim();
		}
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody();
	}

	public String extractRole(String token) {
		return extractClaims(token).get("role", String.class);
	}

	// JWT 토큰에서 사용자 ID 추출
	public Long extractUserId(String token) {
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
	public boolean validateToken(String token) {
		try {
			extractClaims(token);
			return !isTokenExpired(token);
		} catch (Exception e) {
			return false;
		}
	}

	// 토큰 갱신 필요 여부 체크
	public boolean needsRefresh(String token) {
		Date expiration = extractClaims(token).getExpiration();
		return (expiration.getTime() - System.currentTimeMillis()) < refreshThreshold;
	}

	// 토큰 갱신
	public String refreshToken(String token) {
		Claims claims = extractClaims(token);
		claims.setIssuedAt(new Date());
		claims.setExpiration(new Date(System.currentTimeMillis() + expirationTime));

		return Jwts.builder()
			.setClaims(claims)
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}
}