package com.connect.codeness.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageDto {

	private Long senderId;
	private String content;
	private String timestamp;

	@Getter
	@Builder
	public static class ChatRoomCreateRequestDto {

		private final Long partnerId;
	}
}
