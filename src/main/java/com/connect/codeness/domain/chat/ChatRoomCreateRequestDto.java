package com.connect.codeness.domain.chat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomCreateRequestDto {

	private final Long partnerId;
}
