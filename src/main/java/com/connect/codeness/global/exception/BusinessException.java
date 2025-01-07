package com.connect.codeness.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private ExceptionType exceptionType;

	public BusinessException(ExceptionType exceptionType) {
		//exceptionType의 null 여부에 따른 처리
		super(exceptionType != null ? exceptionType.getErrorMessage() : "exceptionType이 null입니다.");
		this.exceptionType = exceptionType; // exceptionType 값을 필드에 설정
	}
}
