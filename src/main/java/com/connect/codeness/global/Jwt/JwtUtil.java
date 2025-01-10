package com.connect.codeness.global.Jwt;

import com.connect.codeness.global.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	// 비밀키 생성
	SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	// 유효시간 설정(1시간)
	private long expirationTime = 1000 * 60 * 60;

	/**
	 * JWT 토큰 생성
	 * @param email 사용자 이메일
	 * @return 생성된 JWT 토큰
	 */
	public String generateToken(String email, Long userId, String userRole) {
		return Jwts.builder()
			.setSubject(email)
			.claim("userId", userId)               // 토큰의 subject에 이메일, 아이디 설정
			.claim("role", userRole)
			.setIssuedAt(new Date())         // 토큰 발급 시간 설정
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 토큰 만료 시간 설정
			.signWith(secretKey, SignatureAlgorithm.HS256) // 비밀키로 서명
			.compact();                      // 토큰 문자열로 변환
	}

	/**
	 * JWT 토큰에서 이메일 추출
	 * @param token JWT 토큰
	 * @return 이메일 (subject)
	 */
	public String extractEmail(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)  // 서명 확인을 위한 비밀키
			.build()
			.parseClaimsJws(token)  // JWT 파싱
			.getBody()
			.getSubject();           // subject에서 이메일 반환
	}

	/**
	 * JWT 토큰에서 아이디 추출
	 * @param token JWT 토큰
	 * @return 아이디
	 */
	public Long extractUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("userId", Long.class);
	}

	/**
	 * JWT 토큰에서 UserRole 추출
	 * @param token
	 * @return
	 */
	public UserRole extractUserRole(String token) {
		Claims claims = getClaims(token);
		claims.get("role", String.class);
		return UserRole.valueOf(claims.get("role", String.class));
	}

	/**
	 * 토큰 만료 여부 체크
	 * @param token JWT 토큰
	 * @return 만료 여부 (true/false)
	 */
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());  // 현재 시간과 만료시간 비교
	}

	/**
	 * JWT 토큰 검증 (이메일 비교 및 만료 체크)
	 * @param token JWT 토큰
	 * @param email 사용자 이메일
	 * @return 토큰 검증 결과 (유효: true, 무효: false)
	 */
	public boolean validateToken(String token, String email) {
		String extractedEmail = extractEmail(token);

		// 이메일이 일치하는지 확인
		if (!extractedEmail.equals(email)) {
			logger.warn("JWT Token email does not match expected email. Extracted: {}, Expected: {}", extractedEmail, email);
			return false;
		}

		// 토큰 만료 여부 체크
		if (isTokenExpired(token)) {
			logger.warn("JWT Token is expired for email: {}", email);
			return false;
		}

		return true;  // 이메일이 일치하고 만료되지 않았다면 유효한 토큰
	}

	/**
	 * 토큰의 만료시간 추출
	 * @param token JWT 토큰
	 * @return 만료 시간
	 */
	private Date extractExpiration(String token) {
		return getClaims(token).getExpiration();
	}

	/**
	 * JWT 토큰에서 Claims (클레임) 추출
	 * @param token JWT 토큰
	 * @return Claims 객체
	 */
	private Claims getClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)  // 서명 검증을 위한 비밀키
				.build()
				.parseClaimsJws(token)  // JWT 파싱
				.getBody();  // 토큰의 클레임 반환
		} catch (JwtException | IllegalArgumentException e) {
			logger.error("Invalid or expired JWT token", e);
			throw new JwtException("Expired or invalid JWT token");
		}
	}
}
