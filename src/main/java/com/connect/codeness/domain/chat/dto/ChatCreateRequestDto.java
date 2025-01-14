package com.connect.codeness.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatCreateRequestDto {

	private final String firebaseChatRoomId;
	private final String message;

}
