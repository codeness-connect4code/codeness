package com.connect.codeness.global.jwt;

import static com.connect.codeness.global.constants.Constants.BEARER;
import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN_EXPIRATION;
import static com.connect.codeness.global.constants.Constants.REFRESH_TOKEN_EXPIRATION;

import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
	SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	/**
	 * 액세스 토큰 생성(인증 용도)
	 *
	 * @param email    유저 이메일
	 * @param userId   유저 고유 식별자
	 * @param role     유저 롤(ADMIN,MENTOR,MENTEE)
	 * @param provider 회원가입 유형
	 * @return 액세스 토큰
	 */
	public String generateAccessToken(String email, Long userId, String role, String provider) {
		return Jwts.builder().setSubject(email).claim("userId", userId).claim("role", role)
			.claim("provider", provider).setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
			.signWith(secretKey).compact();
	}

	/**
	 * 리프레시 토큰 생성(갱신 용도)
	 *
	 * @param userId 유저 고유 식별자
	 * @return 리프레시 토큰
	 */
	public String generateRefreshToken(Long userId) {
		return Jwts.builder().setSubject(userId.toString()).setIssuedAt(new Date()) //생성일
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION)) //갱신일
			.signWith(secretKey).compact();
	}

	/**
	 * 토큰에서 Barer 접두사 제거 메서드
	 *
	 * @param token 기본 토큰
	 * @return 접두사가 제거된 토큰 string
	 */
	public String extractBearer(String token) {
		if (token.startsWith(BEARER)) {
			token = token.substring(BEARER.length()).trim();
		}
		return token;
	}

	/**
	 * 토큰에서 클레임 추출 메서드
	 *
	 * @param token 액세스 토큰
	 * @return 토큰에서 추출한 클레임
	 */
	public Claims extractClaims(String token) {
		token = extractBearer(token);
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
				.getBody();
		} catch (JwtException e) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
	}

	/**
	 * 토큰에서 유저 role 추출 메서드
	 *
	 * @param token 액세스 토큰
	 * @return 유저 role
	 */
	public String extractRole(String token) {
		try {
			return extractClaims(token).get("role", String.class);
		} catch (JwtException e) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
	}

	/**
	 * 토큰에서 유저 고유 식별자 추출 메서드
	 *
	 * @param token 액세스 토큰
	 * @return 유저 고유 식별자
	 */
	public Long extractUserId(String token) {
		try {
			return extractClaims(token).get("userId", Long.class);
		} catch (JwtException e) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
	}

	/**
	 * 토큰에서 유저 이메일 추출 메서드
	 *
	 * @param token 액세스 토큰
	 * @return 유저 이메일
	 */
	public String extractEmail(String token) {
		try {
			return extractClaims(token).getSubject();
		} catch (JwtException e) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}

	}

	/**
	 * 엑세스 토큰 검증 메서드
	 *
	 * @param token 액세스 토큰
	 * @return 검증 여부
	 */
	public boolean validationAccessToken(String token) {
		try {
			Claims claims = extractClaims(token);
			return !claims.getExpiration().before(new Date());
		} catch (JwtException e) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
	}

	/**
	 * 리프레시 토큰 검증 메서드
	 *
	 * @param token 리프레시 토큰
	 * @return 검증 여부
	 */
	public boolean validationRefreshToken(String token) {
		try {
			Claims claims = extractClaims(token);
			return !claims.getExpiration().before(new Date());
		} catch (JwtException e) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
	}

	/**
	 * 엑세스 토큰 갱신 메서드
	 *
	 * @param refreshToken 리프레시 토큰
	 * @param email        유저 이메일
	 * @param role         유저 role
	 * @param provider     유저 회원가입 유형
	 * @return 갱신된 엑세스 토큰
	 */
	public String regenerateAccessToken(String refreshToken, String email, String role,
		String provider) {
		Claims claims = extractClaims(refreshToken);
		Long userId = Long.parseLong(claims.getSubject());

		return generateAccessToken(email, userId, role, provider);
	}

	/**
	 * HTTP-Only 쿠키 생성
	 *
	 * @param name  토큰 이름
	 * @param value 토큰
	 * @return 쿠키
	 */
	public jakarta.servlet.http.Cookie createHttpOnlyCookie(String name, String value, int maxAge) {
		jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		return cookie;
	}

	public Long getCookieReturnUserId(HttpServletRequest request, String name) {
		String token = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					token = cookie.getValue();
					break;
				}
			}
		}

		if (token == null) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
		return extractUserId(token);
	}

//	// JWT 토큰 만료 여부 체크
//	public boolean isTokenExpired(String token) {
//		return extractClaims(token).getExpiration().before(new Date());
//	}
//
//	// JWT 토큰 검증
//	public boolean validateToken(String token) {
//		try {
//			extractClaims(token);
//			return !isTokenExpired(token);
//		} catch (Exception e) {
//			throw new BusinessException(ExceptionType.INVALID_TOKEN);
//		}
//	}
//
//	// 토큰 갱신 필요 여부 체크
//	public boolean needsRefresh(String token) {
//		Date expiration = extractClaims(token).getExpiration();
//		return (expiration.getTime() - System.currentTimeMillis()) < refreshThreshold;
//	}
//
//	// 토큰 갱신
//	public String refreshToken(String token) {
//		Claims claims = extractClaims(token);
//		claims.setIssuedAt(new Date());
//		claims.setExpiration(new Date(System.currentTimeMillis() + expirationTime));
//
//		return Jwts.builder()
//			.setClaims(claims)
//			.signWith(SignatureAlgorithm.HS256, secretKey)
//			.compact();
//	}
}