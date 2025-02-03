package com.connect.codeness.domain.chat.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomCreateRequestDto {

	private final Long paymentHistoryId;
	private final LocalDate mentoringDate;
	private final LocalTime mentoringTime;
}
