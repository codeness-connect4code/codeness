package com.connect.codeness.domain.calendar.service;

import com.connect.codeness.domain.calendar.dto.CalendarEventDto;
import com.connect.codeness.domain.calendar.dto.CalendarEventStringDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;

public interface CalendarService {
	List<CalendarEventDto> getEvents(Long tokenId, String startDate, String endDate);
	CommonResponseDto<?> createEvent(Long userId, CalendarEventStringDto eventDto);
}
