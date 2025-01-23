package com.connect.codeness.domain.calendar.service;

import com.connect.codeness.domain.calendar.dto.CalendarEventDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {
	private final UserRepository userRepository;
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String APPLICATION_NAME = "Codeness Calendar";
	private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

	public CalendarServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public List<CalendarEventDto> getEvents(Long tokenId, String startDate, String endDate) {
		try {
			User user = userRepository.findById(tokenId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

			Credential credential = getCredentials(GoogleNetHttpTransport.newTrustedTransport()); // ✅ 인증 정보 가져오기
			Calendar service = getCalendarService(credential); // ✅ 인증을 통한 Calendar 서비스 객체 생성

			DateTime start = DateTime.parseRfc3339(startDate + "T00:00:00Z");
			DateTime end = DateTime.parseRfc3339(endDate + "T23:59:59Z");

			Events events = service.events().list("primary")
				.setTimeMin(start)
				.setTimeMax(end)
				.setOrderBy("startTime")
				.setSingleEvents(true)
				.execute();

			return events.getItems().stream()
				.map(this::mapToCalendarEvent)
				.collect(Collectors.toList());
		} catch (IOException | GeneralSecurityException e) {
			throw new RuntimeException("Failed to fetch calendar events", e);
		}
	}

	// Credential을 인자로 받는 메서드
	private Calendar getCalendarService(Credential credential) throws GeneralSecurityException, IOException {
		return new Calendar.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			GsonFactory.getDefaultInstance(),
			credential)
			.setApplicationName(APPLICATION_NAME)
			.build();
	}

	// 인증 정보를 가져오는 메서드
	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		InputStream in = CalendarServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("credentials.json not found");
		}

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
			HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
			.setAccessType("offline")
			.build();

		LocalServerReceiver receiver = new LocalServerReceiver.Builder()
			.setPort(-1) // 랜덤 포트 사용
			.build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	// Event 객체를 DTO로 변환하는 메서드
	private CalendarEventDto mapToCalendarEvent(Event event) {
		return CalendarEventDto.builder()
			.id(event.getId())
			.title(event.getSummary())
			.summary(event.getSummary())
			.description(event.getDescription())
			.startTime(event.getStart().getDateTime())
			.endTime(event.getEnd().getDateTime())
			.build();
	}
}
