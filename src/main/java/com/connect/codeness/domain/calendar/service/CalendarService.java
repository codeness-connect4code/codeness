package com.connect.codeness.domain.calendar.service;

import com.connect.codeness.domain.calendar.dto.CalendarEventDto;
import java.util.List;

public interface CalendarService {
	List<CalendarEventDto> getEvents(Long tokenId, String startDate, String endDate);
}
