package com.connect.codeness.domain.calendar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalendarEventResponseDto {
	private String id;
	private String title;
	private String description;
	private String startTime;
	private String endTime;

	public static CalendarEventResponseDto from(CalendarEventDto event) {
		return CalendarEventResponseDto.builder()
			.id(event.getId())
			.title(event.getSummary())
			.description(event.getDescription())
			.startTime(event.getStartTime().toString())
			.endTime(event.getEndTime().toString())
			.build();
	}
}
