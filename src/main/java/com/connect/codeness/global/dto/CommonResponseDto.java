package com.connect.codeness.global.dto;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class CommonResponseDto<T> {

	private String msg;
	private T data;

}