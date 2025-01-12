package com.connect.codeness.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageDto {

	private Long senderId;
	private String content;
	private long timestamp;

}
