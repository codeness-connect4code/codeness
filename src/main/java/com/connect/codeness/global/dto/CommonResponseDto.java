package com.connect.codeness.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResponseDto<T> {

	private String msg;
	private T data;
}
