package com.connect.codeness.domain.calendar.controller;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.calendar.dto.CalendarEventDto;
import com.connect.codeness.domain.calendar.dto.CalendarEventResponseDto;
import com.connect.codeness.domain.calendar.service.CalendarService;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.jwt.JwtUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class CalendarController {
	private final CalendarService calendarService;
	private final JwtUtil jwtUtil;

	public CalendarController(final CalendarService calendarService, JwtUtil jwtUtil) {
		this.calendarService = calendarService;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * CalendarService에서 구글 캘린더 일정을 리스트로 받아옴
	 * @param authorizationHeader
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/schedule")
	public ResponseEntity<CommonResponseDto> getEvents(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@RequestParam String startDate,
		@RequestParam String endDate
	) {
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);

		//캘린더 서비스에서 특정 기간 일정 조회
		List<CalendarEventDto> events = calendarService.getEvents(tokenId, startDate, endDate);

		//응답 객체로 반환
		List<CalendarEventResponseDto> responses = events.stream()
			.map(CalendarEventResponseDto::from)
			.collect(Collectors.toList());

		CommonResponseDto commonResponseDto = CommonResponseDto.builder()
			.msg("스케쥴 이벤트가 조회되었습니다.")
			.data(responses)
			.build();

		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}
}
