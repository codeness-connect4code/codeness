package com.connect.codeness.domain.calendar.dto;

import com.google.api.client.util.DateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalendarEventDto {
	private String id;
	private String title;
	private String summary;
	private String description;
	private DateTime startTime;
	private DateTime endTime;
}
