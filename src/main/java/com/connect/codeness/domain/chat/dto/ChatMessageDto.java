package com.connect.codeness.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageDto {

	private Long senderId;
	private String content;
	private String timestamp;

}
