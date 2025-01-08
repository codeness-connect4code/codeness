package com.connect.codeness.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

	// 400 Bad Request
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다."),
	INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
	ALEADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST,"이미 사용중인 이메일 입니다."),
	// 401 Unauthorized
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	UNAUTHORIZED_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 틀렸습니다."),

	// 403 Forbidden
	FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN,"접근이 거부됐습니다."),
	FORBIDDEN_PERMISSION(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다."),
	FORBIDDEN_LOGIN(HttpStatus.FORBIDDEN,"이미 탈퇴한 유저입니다."),
	NOT_YOUR_COMMENT(HttpStatus.FORBIDDEN,"해당 댓글의 작성자가 아닙니다."),

	// 404 NOT_FOUND
	NOT_FOUND(HttpStatus.NOT_FOUND,"리소스를 찾을 수 없습니다."),
	NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
	NOT_FOUND_PAYMENTLIST(HttpStatus.NOT_FOUND, "거래 내역을 찾을 수 없습니다."),
	// 409 CONFLICT
	DUPLICATE_VALUE(HttpStatus.CONFLICT, "중복된 정보입니다."),
	USER_ALREADY_DELETED(HttpStatus.CONFLICT, "이미 탈퇴한 사용자 아이디입니다."),

	;


    private final HttpStatus httpStatus;
	private final String errorMessage;

	ExceptionType(HttpStatus httpStatus, String errorMessage) {
		this.httpStatus = httpStatus;
		this.errorMessage = errorMessage;
	}
}
