package com.connect.codeness.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

	// 400 Bad Request
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다."),
	INVALID_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
	ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일 입니다."),
	MENTOR_REQUEST_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "한 명당 하나의 신청만 가능합니다."),
	TOO_EARLY_REVIEW(HttpStatus.BAD_REQUEST, "아직 후기를 작성할 수 없습니다."),
	NOT_SUPPORT_EXTENSION(HttpStatus.BAD_REQUEST, "올바른 확장자가 아닙니다."),
	INVALID_PAYMENT(HttpStatus.BAD_REQUEST, "결제가 유효하지 않습니다."),
	MISSING_API_KEY(HttpStatus.BAD_REQUEST, "API KEY 값이 누락되었습니다."),
	ALREADY_CANCEL(HttpStatus.BAD_REQUEST, "이미 결제가 취소된 내역입니다."),
	REFUND_FAILED(HttpStatus.BAD_REQUEST, "환불 요청이 실패했습니다."),
	ALREADY_BOOKED(HttpStatus.BAD_REQUEST, "이미 예약된 멘토링 스케쥴 입니다." ),
	MENTORING_SCHEDULE_EXPIRED(HttpStatus.BAD_REQUEST, "멘토링 스케쥴 시간이 만료되었습니다." ),

	// 401 Unauthorized
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	UNAUTHORIZED_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 틀렸습니다."),
	UNAUTHORIZED_DELETE_REQUEST(HttpStatus.UNAUTHORIZED, "권한이 없는 삭제 요청입니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),

	// 403 Forbidden
	FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근이 거부됐습니다."),
	FORBIDDEN_PERMISSION(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다."),
	FORBIDDEN_LOGIN(HttpStatus.FORBIDDEN, "이미 탈퇴한 유저입니다."),
	NOT_YOUR_COMMENT(HttpStatus.FORBIDDEN, "해당 댓글의 작성자가 아닙니다."),
	FORBIDDEN_PAYMENT_ACCESS(HttpStatus.FORBIDDEN, "다른 사용자의 결제 내역을 조회할 수 없습니다."),
	FORBIDDEN_SETTLEMENT_ACCESS(HttpStatus.FORBIDDEN, "정산 신청을 할 수 있는 권한이 없습니다."),

	// 404 NOT_FOUND
	NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
	NOT_FOUND_POST(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
	NOT_FOUND_PAYMENTLIST(HttpStatus.NOT_FOUND, "거래 내역을 찾을 수 없습니다."),
	NOT_FOUND_NEWS(HttpStatus.NOT_FOUND,"뉴스를 찾을 수 없습니다."),
	NOT_FOUND_USER(HttpStatus.NOT_FOUND,"유저를 찾을 수 없습니다."),
	NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "후기를 찾을 수 없습니다."),
	NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
	NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다."),
	NOT_FOUND_MENTORING_SCHEDULE(HttpStatus.NOT_FOUND, "멘토링 스케쥴을 찾을 수 없습니다."),
	NOT_FOUND_IMPUID(HttpStatus.NOT_FOUND, "imp_uid를 찾을 수 없습니다."),
	NOT_FOUND_PGTID(HttpStatus.NOT_FOUND, "pg_tid를 찾을 수 없습니다."),
	NOT_FOUND_AMOUNT(HttpStatus.NOT_FOUND, "결제 금액이 올바르지 않습니다."),
	NOT_FOUND_PAYMENT_BY_IAMPORT(HttpStatus.NOT_FOUND, "아임포트에서 결제 내역을 조회할 수 없습니다."),
	NOT_FOUND_SETTLEMENT_DATE(HttpStatus.NOT_FOUND, "정산 가능한 결제 내역이 없습니다."),

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
