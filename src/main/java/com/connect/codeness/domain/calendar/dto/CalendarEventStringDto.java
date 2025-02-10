package com.connect.codeness.domain.calendar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalendarEventStringDto {
	private String id;
	private String title;
	private String summary;
	private String description;
	private String startTime;
	private String endTime;
	public static CalendarEventStringDto from(CalendarEventDto event) {
		return CalendarEventStringDto.builder()
			.id(event.getId())
			.title(event.getSummary())
			.description(event.getDescription())
			.startTime(event.getStartTime() != null ? event.getStartTime().toString() : null)
			.endTime(event.getEndTime() != null ? event.getEndTime().toString() : null)
			.build();
	}
}
