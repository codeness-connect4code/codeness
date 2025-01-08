package com.connect.codeness.global.enums;

import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Arrays;

public enum FieldType {
	BACKEND,
	FRONTEND;

	/**
	 * 유저가 입력한 FieldType enum으로 변환하는 메서드
	 * TODO : ExceptionType 일단 NOT_FOUND로 해둠, 추후 수정 가능
	 */
	public static FieldType fromString(String name) {
		return Arrays.stream(values()).filter(fieldType -> fieldType.name().equalsIgnoreCase(name))
									  .findFirst()
			                          .orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND));
	}
}
