package com.connect.codeness.global.dto;

public class CommonResponse<T> {

	private String msg;
	private T data;

	public CommonResponse(String msg, T data) {
		this.msg = msg;
		this.data = data;
	}
}
