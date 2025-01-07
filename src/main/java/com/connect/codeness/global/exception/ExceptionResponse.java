package com.connect.codeness.global.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionResponse {

	private final HttpStatus httpStatus;
	private final int statusCode;
	private final Map<String, String> errors;

	public ExceptionResponse(HttpStatus httpStatus, int statusCode, Map<String, String> errors) {
		this.httpStatus = httpStatus;
		this.statusCode = statusCode;
		this.errors = errors;
	}
}
