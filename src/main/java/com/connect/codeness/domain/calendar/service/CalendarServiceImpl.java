package com.connect.codeness.domain.calendar.service;

import com.connect.codeness.domain.calendar.dto.CalendarEventDto;
import com.connect.codeness.domain.calendar.dto.CalendarEventStringDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {
	private final UserRepository userRepository;
	private static final String CREDENTIALS_FILE_PATH = "/app/credentials.json";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String APPLICATION_NAME = "Codeness Calendar";
	private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

	public CalendarServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public List<CalendarEventDto> getEvents(Long tokenId, String startDate, String endDate) {
		try {
			User user = userRepository.findByIdOrElseThrow(tokenId);
			validateGoogleToken(user);

			Calendar service = initializeCalendarService(user.getGoogleToken());

			DateTime start = DateTime.parseRfc3339(startDate + "T00:00:00Z");
			DateTime end = DateTime.parseRfc3339(endDate + "T23:59:59Z");

			Events events = service.events().list("primary")
				.setTimeMin(start)
				.setTimeMax(end)
				.setOrderBy("startTime")
				.setSingleEvents(true)
				.execute();

			List<CalendarEventDto> eventList = Optional.ofNullable(events.getItems())
				.orElse(Collections.emptyList())  // 일정이 없을 경우 빈 리스트 반환
				.stream()
				.map(this::mapToCalendarEvent)
				.collect(Collectors.toList());

			log.info("조회된 일정 수: {}", eventList.size());
			return eventList;

		} catch (IOException | GeneralSecurityException e) {
			log.error("캘린더 이벤트 조회 실패", e);
			throw new BusinessException(ExceptionType.GOOGLE_CALENDAR_ERROR);
		}
	}

	@Override
	public CommonResponseDto<?> createEvent(Long userId, CalendarEventStringDto eventDto) {
		try {
			User user = userRepository.findByIdOrElseThrow(userId);
			validateGoogleToken(user);

			Calendar service = initializeCalendarService(user.getGoogleToken());
			Event event = createEventFromDto(eventDto);
			Event createdEvent = service.events().insert("primary", event).execute();

			return CommonResponseDto.builder()
				.msg("일정 생성 완료")
				.data(mapToCalendarEventStringDto(createdEvent))
				.build();

		} catch (GeneralSecurityException | IOException e) {
			log.error("Google Calendar 이벤트 생성 실패", e);
			throw new BusinessException(ExceptionType.GOOGLE_CALENDAR_ERROR);
		}
	}

	private Calendar initializeCalendarService(String googleToken) throws IOException, GeneralSecurityException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Credential credential = getCredentials(googleToken, HTTP_TRANSPORT);
		return getCalendarService(credential);
	}

	private void validateGoogleToken(User user) {
		if (user.getGoogleToken() == null) {
			throw new BusinessException(ExceptionType.NOT_FOUND_GOOGLE_TOKEN);
		}
	}

	private Event createEventFromDto(CalendarEventStringDto eventDto) {
		DateTime startDateTime = new DateTime(eventDto.getStartTime());
		DateTime endDateTime = new DateTime(eventDto.getEndTime());

		EventDateTime start = new EventDateTime()
			.setDateTime(startDateTime)
			.setTimeZone("Asia/Seoul");

		EventDateTime end = new EventDateTime()
			.setDateTime(endDateTime)
			.setTimeZone("Asia/Seoul");

		return new Event()
			.setSummary(eventDto.getSummary())
			.setDescription(eventDto.getDescription())
			.setStart(start)
			.setEnd(end);
	}

	private Calendar getCalendarService(Credential credential) throws GeneralSecurityException, IOException {
		return new Calendar.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			GsonFactory.getDefaultInstance(),
			credential)
			.setApplicationName(APPLICATION_NAME)
			.build();
	}

	private Credential getCredentials(String accessToken, final NetHttpTransport HTTP_TRANSPORT) throws IOException {

		InputStream in = loadCredentials();

//		InputStream in = CalendarServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//		if (in == null) {
//			throw new FileNotFoundException("credentials.json not found");
//		}

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		GoogleCredential credential = new GoogleCredential.Builder()
			.setTransport(HTTP_TRANSPORT)
			.setJsonFactory(JSON_FACTORY)
			.setClientSecrets(clientSecrets.getDetails().getClientId(),
				clientSecrets.getDetails().getClientSecret())
			.build()
			.setAccessToken(accessToken);

		if (credential.getAccessToken() == null) {
			throw new BusinessException(ExceptionType.NOT_FOUND_GOOGLE_TOKEN);
		}

		return credential;
	}

	public InputStream loadCredentials() throws IOException {

		Path path = Paths.get(CREDENTIALS_FILE_PATH);
		if (!Files.exists(path)) {
			throw new FileNotFoundException("파일을 찾을 수 없습니다. : " + CREDENTIALS_FILE_PATH);
		}

		System.out.println("파일이 성공적으로 로드되었습니다.");
		return Files.newInputStream(path);
	}

	private CalendarEventDto mapToCalendarEvent(Event event) {
		return CalendarEventDto.builder()
			.id(event.getId())
			.title(event.getSummary())
			.summary(event.getSummary())
			.description(event.getDescription())
			.startTime(Optional.ofNullable(event.getStart())
				.map(EventDateTime::getDateTime)
				.orElse(null))
			.endTime(Optional.ofNullable(event.getEnd())
				.map(EventDateTime::getDateTime)
				.orElse(null))
			.build();
	}

	private CalendarEventStringDto mapToCalendarEventStringDto(Event event) {
		return CalendarEventStringDto.builder()
			.id(event.getId())
			.summary(event.getSummary())
			.description(event.getDescription())
			.startTime(Optional.ofNullable(event.getStart())
				.map(EventDateTime::getDateTime)
				.map(DateTime::toString)
				.orElse(null))
			.endTime(Optional.ofNullable(event.getEnd())
				.map(EventDateTime::getDateTime)
				.map(DateTime::toString)
				.orElse(null))
			.build();
	}
}
