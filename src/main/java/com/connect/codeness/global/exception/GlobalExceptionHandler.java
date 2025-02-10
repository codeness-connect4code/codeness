package com.connect.codeness.global.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({BusinessException.class})
	public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException exception) {
		//BusinessException에서 ExceptionType을 가져오기(상태코드 & 에러메세지)
		ExceptionType exceptionType = exception.getExceptionType();

		//Map 생성(key, value 저장)
		Map<String, String> errors = new HashMap<>();
		//errors에 이름과 에러 메세지를 추가
		errors.put(exceptionType.name(), exceptionType.getErrorMessage());

		//정보 담을 객체 생성(상태코드, 코드 값, 에러 정보)
		ExceptionResponse exceptionResponse = new ExceptionResponse(exceptionType.getHttpStatus(),
			exceptionType.getHttpStatus().value(), errors);

		//로그 기록
		log.error("[ {} ] {} : {}", exception.getClass(), exceptionType.getHttpStatus(),
			exceptionResponse.getErrors());

		return new ResponseEntity<>(exceptionResponse, exceptionType.getHttpStatus());
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){

		//Map 생성(key, value 저장)
		Map<String, String> errors = new HashMap<>();
		//errors에 이름과 에러 메세지를 추가
		errors.put("VALIDATION_FAIL", e.getBindingResult()
			.getAllErrors()
			.stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.filter(Objects::nonNull)
			.findFirst()
			.orElse("Validation failed"));

		//정보 담을 객체 생성(상태코드, 코드 값, 에러 정보)
		ExceptionResponse exceptionResponse = new ExceptionResponse((HttpStatus) e.getStatusCode(),
			e.getStatusCode().value(), errors);

		//로그 기록
		log.error("[ {} ] {} : {}", e.getClass(),e.getStatusCode(),
			exceptionResponse.getErrors());

		return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
	}

}

