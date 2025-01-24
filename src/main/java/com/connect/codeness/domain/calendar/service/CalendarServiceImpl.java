package com.connect.codeness.domain.calendar.service;

import com.connect.codeness.domain.calendar.dto.CalendarEventDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
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

	/**
	 * 구글 캘린더 일정을 리스트로 조회
	 * @param tokenId 유저 고유 식별자
	 * @param startDate 조회 시작 날짜
	 * @param endDate 조회 끝 날짜
	 * @return dto 리스트
	 */
	@Override
	public List<CalendarEventDto> getEvents(Long tokenId, String startDate, String endDate) {
		try {
			User user = userRepository.findByIdOrElseThrow(tokenId);

			//유저에 구글 토큰이 없을시 예외처리
			if (user.getGoogleToken() == null) {
				throw new BusinessException(ExceptionType.NOT_FOUND_GOOGLE_TOKEN);
			}

			//OAuth2 구글 토큰을 기반으로 Credential 객체 생성
			Credential credential = getCredentials(user.getGoogleToken(), GoogleNetHttpTransport.newTrustedTransport());

			//구글 캘린더 객체 생성
			Calendar service = getCalendarService(credential);

			//조회할 날짜를 설정, 구글 캘린더의 RFC 3339 형식으로 변환
			DateTime start = DateTime.parseRfc3339(startDate + "T00:00:00Z");
			DateTime end = DateTime.parseRfc3339(endDate + "T23:59:59Z");

			//구글 캘린더 API 사용해 범위내의 일정들 조회
			Events events = service.events().list("primary") //기본 캘린더(primary)에서 조회
				.setTimeMin(start)
				.setTimeMax(end)
				.setOrderBy("startTime")
				.setSingleEvents(true) //반복 이벤트를 개별 이벤트로 분리해 가져옴
				.execute();

			//일정 목록을 DTO 리스트로 변환해 반환
			return events.getItems().stream()
				.map(this::mapToCalendarEvent)
				.collect(Collectors.toList());
		} catch (IOException | GeneralSecurityException e) {
			throw new BusinessException(ExceptionType.NOT_FOUND);
		}
	}

	/**
	 * Credential 객체를 구글 Calendar 객체로 변환
	 * @param credential
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private Calendar getCalendarService(Credential credential) throws GeneralSecurityException, IOException {
		return new Calendar.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),  //HTTP 트랜스포트 설정
			GsonFactory.getDefaultInstance(),  //JSON 파서 설정
			credential) //인증 정보 설정
			.setApplicationName(APPLICATION_NAME) //애플리케이션 이름 설정
			.build();
	}

	/**
	 * 구글 토큰에서 인증정보를 가져옴
	 * @param accessToken
	 * @param HTTP_TRANSPORT
	 * @return
	 * @throws IOException
	 */
	private Credential getCredentials(String accessToken, final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		//resources 의 credentials.json 파일을 읽어옴
		InputStream in = CalendarServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("credentials.json not found");  //json 파일이 없을시 예외처리
		}

		//구글 클라이언트 시크릿 정보를 로드
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		//구글 OAuth2 인증정보를 기반으로 Credential 객체 생성
		GoogleCredential credential = new GoogleCredential.Builder()
			.setTransport(HTTP_TRANSPORT)
			.setJsonFactory(JSON_FACTORY)
			.setClientSecrets(clientSecrets.getDetails().getClientId(),
				clientSecrets.getDetails().getClientSecret())
			.build()
			.setAccessToken(accessToken);

		if (credential.getAccessToken() == null) {
			throw new BusinessException(ExceptionType.NOT_FOUND_GOOGLE_TOKEN); //구글 토큰 없을시 예외처리
		}

		return credential;
	}

	/**
	 * event 객체를 캘린더 dto로 변환
	 * @param event
	 * @return
	 */
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
