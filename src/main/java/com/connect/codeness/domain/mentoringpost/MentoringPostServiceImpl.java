package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.domain.mentoringschedule.MentoringSchedule;
import com.connect.codeness.domain.mentoringschedule.MentoringScheduleRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.BookedStatus;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MentoringPostServiceImpl implements MentoringPostService {

	private final MentoringPostRepository mentoringPostRepository;
	private final UserRepository userRepository;
	private final MentoringScheduleRepository mentoringScheduleRepository;

	public MentoringPostServiceImpl(MentoringPostRepository mentoringPostRepository,
		UserRepository userRepository, MentoringScheduleRepository mentoringScheduleRepository) {
		this.mentoringPostRepository = mentoringPostRepository;
		this.userRepository = userRepository;
		this.mentoringScheduleRepository = mentoringScheduleRepository;
	}

	/**
	 * 멘토링 공고 생성 서비스 메서드
	 */
	@Override
	@Transactional
	public CommonResponseDto createMentoringPost(long userId, MentoringPostCreateRequestDto requestDto) {
		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		//유저가 멘티일 경우 예외처리
		if (user.getRole().equals(UserRole.MENTEE)) {
			throw new BusinessException(ExceptionType.FORBIDDEN_MENTORING_CREATE_ACCESS);
		}

		// 분야 타입 변환
		FieldType fieldType = FieldType.fromString(requestDto.getField().name());

		MentoringPost mentoringPost = MentoringPost.builder()
			.user(user)
			.title(requestDto.getTitle())
			.company(requestDto.getCompany())
			.field(fieldType)
			.career(requestDto.getCareer())
			.region(requestDto.getRegion())
			.price(requestDto.getPrice())
			.startDate(requestDto.getStartDate())
			.endDate(requestDto.getEndDate())
			.startTime(requestDto.getStartTime())
			.endTime(requestDto.getEndTime())
			.description(requestDto.getDescription())
			.build();

		//db 저장
		mentoringPostRepository.save(mentoringPost);

		//멘토링 스케쥴 생성
		List<MentoringSchedule> schedules = createMentoringSchedules(
			requestDto.getStartDate(),
			requestDto.getEndDate(),
			requestDto.getStartTime(),
			requestDto.getEndTime(),
			mentoringPost
		);
		//DB 저장
		mentoringScheduleRepository.saveAll(schedules);

		return CommonResponseDto.builder().msg("멘토링 공고 생성이 완료되었습니다.").build();
	}
	
	/**
	 * 멘토링 공고 - 멘토링 스케쥴 생성 메서드
	 * - 메서드 분리
	 */
	private List<MentoringSchedule> createMentoringSchedules(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
															MentoringPost mentoringPost) {

		return startDate.datesUntil(endDate.plusDays(1)) // 날짜 범위 스트림 생성
			.flatMap(date -> {
				long hourCount = ChronoUnit.HOURS.between(startTime, endTime.plusHours(1)); // 시간 범위 계산
				return LongStream.range(0, hourCount)
					.mapToObj(hour -> {
						MentoringSchedule schedule = new MentoringSchedule();
						schedule.createMentoringSchedule(mentoringPost, date, startTime.plusHours(hour), BookedStatus.EMPTY);
						return schedule;
					});
			}).collect(Collectors.toList());
	}

	/**
	 * 멘토링 공고 삭제 서비스 메서드
	 */
	@Override
	public CommonResponseDto deleteMentoringPost(Long userId, Long mentoringPostId) {

		//멘토링 공고 조회
		MentoringPost mentoringPost = mentoringPostRepository.findById(mentoringPostId)
			.orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTORING_POST));

		//멘토링 공고 작성한 유저랑 로그인한 유저랑 비교
		if (!Objects.equals(userId, mentoringPost.getUser().getId())) {
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}

		//멘토링 공고 삭제
		mentoringPostRepository.deleteById(mentoringPost.getId());

		return CommonResponseDto.builder().msg("멘토링 공고가 삭제되었습니다.").build();
	}


}
