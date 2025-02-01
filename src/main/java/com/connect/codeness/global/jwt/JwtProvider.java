package com.connect.codeness.global.jwt;

import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN_EXPIRATION;
import static com.connect.codeness.global.constants.Constants.BEARER;
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

	public String generateAccessToken(String email, Long userId, String role, String provider) {
		return Jwts.builder().setSubject(email).claim("userId", userId).claim("role", role)
			.claim("provider", provider).setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
			.signWith(secretKey).compact();
	}

	public String generateRefreshToken(Long userId) {
		return Jwts.builder().setSubject(userId.toString()).setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
			.signWith(secretKey).compact();
	}

	public String extractBearer(String token) {
		if (token != null && token.startsWith(BEARER)) {
			return token.substring(BEARER.length()).trim();
		}
		return token;
	}

	public Claims extractClaims(String token) {
		token = extractBearer(token);
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
				.getBody();
		} catch (JwtException e) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
	}

	public String extractRole(String token) {
		return extractClaims(token).get("role", String.class);
	}

	public Long extractUserId(String token) {
		return extractClaims(token).get("userId", Long.class);
	}

	public String extractUserIdFromRefresh(String token) {
		return extractClaims(token).getSubject();
	}

	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}

	public String extractProvider(String token) {
		return extractClaims(token).get("provider", String.class);
	}

	public boolean validationAccessToken(String token) {
		Claims claims = extractClaims(token);
		return !claims.getExpiration().before(new Date());
	}

	public boolean validationRefreshToken(String token) {
		Claims claims = extractClaims(token);
		return !claims.getExpiration().before(new Date());
	}

	public String regenerateAccessToken(String refreshToken, String email, String role,
		String provider) {
		if (!validationRefreshToken(refreshToken)) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
		Claims claims = extractClaims(refreshToken);
		Long userId = Long.parseLong(claims.getSubject());
		return generateAccessToken(email, userId, role, provider);
	}

	public Cookie createHttpOnlyCookie(String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setAttribute("SameSite", "None");
		cookie.setMaxAge(maxAge);
		return cookie;
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith(BEARER)) {
			return bearerToken.substring(BEARER.length());
		}
		return null;
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
		if (token == null || !validationAccessToken(token)) {
			throw new BusinessException(ExceptionType.INVALID_TOKEN);
		}
		return extractUserId(token);
	}
}
