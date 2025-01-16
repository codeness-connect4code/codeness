package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostDetailResponseDto;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostSearchResponseDto;
import com.connect.codeness.domain.mentoringpost.dto.PaginationResponseDto;
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
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	 * 멘토링 공고 - 멘토링 스케쥴 생성 서비스 메서드
	 * - 메서드 분리
	 */
	private List<MentoringSchedule> createMentoringSchedules(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
		MentoringPost mentoringPost) {

		return startDate.datesUntil(endDate.plusDays(1)) // 날짜 범위 스트림 생성
			.flatMap(date -> {
				boolean crossesMidnight = endTime.isBefore(startTime);//시간 하루 넘기는지 확인
				long hourCount = crossesMidnight
					? ChronoUnit.HOURS.between(startTime, LocalTime.MAX) + 1 + ChronoUnit.HOURS.between(LocalTime.MIN, endTime.plusHours(1))
					: ChronoUnit.HOURS.between(startTime, endTime.plusHours(1));

				return LongStream.range(0, hourCount)
					.mapToObj(hour -> {
						LocalDateTime dateTime = date.atTime(startTime).plusHours(hour);

						//날짜 넘어가는 경우
						if (dateTime.toLocalTime().isBefore(startTime)) {
							dateTime = dateTime.plusDays(1);
						}

						MentoringSchedule schedule = new MentoringSchedule();
						schedule.createMentoringSchedule(mentoringPost, date, startTime.plusHours(hour), BookedStatus.EMPTY);
						return schedule;
					});
			}).collect(Collectors.toList());
	}

	/**
	 * 멘토링 공고 삭제 서비스 메서드
	 */
	@Transactional
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

	/**
	 * 멘토링 공고 전체 조회 서비스 메서드
	 * - TODO : 평균 별점 추가, 응답 필드 수정
	 */
	@Override
	public CommonResponseDto<PaginationResponseDto<MentoringPostSearchResponseDto>> searchMentoringPosts(int pageNumber, int pageSize,
		String title, String field,
		String nickname) {
		//페이징
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		//멘토링 공고 조회
		Page<MentoringPost> mentoringPosts = mentoringPostRepository.findAllBySearchParameters(title, field, nickname, pageable);

		//dto 변환 page -> PaginationResponseDto
		List<MentoringPostSearchResponseDto> mentoringPostResponseDtos = mentoringPosts.getContent().stream()
			.map(mentoringPost -> MentoringPostSearchResponseDto.builder()
				.mentoringPostId(mentoringPost.getId())
				.userNickname(mentoringPost.getUser().getUserNickname())
				.field(mentoringPost.getField())
				.title(mentoringPost.getTitle())
				.career(mentoringPost.getCareer())
				.build()).toList();

		//PaginationResponseDto 생성
		PaginationResponseDto<MentoringPostSearchResponseDto> paginationResponseDto = PaginationResponseDto.<MentoringPostSearchResponseDto>builder()
			.content(mentoringPostResponseDtos)
			.totalPages(mentoringPosts.getTotalPages())
			.totalElements(mentoringPosts.getTotalElements())
			.pageNumber(mentoringPosts.getNumber())
			.pageSize(mentoringPosts.getSize())
			.build();

		return CommonResponseDto.<PaginationResponseDto<MentoringPostSearchResponseDto>>builder()
			.msg("멘토링 공고가 전체 조회되었습니다").data(paginationResponseDto).build();
	}

	/**
	 * 멘토링 공고 상세 조회 서비스 메서드
	 * - 모든 유저 가능 -TODO : 평균 별점 조회 추가
	 */
	@Override
	public CommonResponseDto<MentoringPostDetailResponseDto> getMentoringPostDetail(Long mentoringPostId) {
		MentoringPost mentoringPost = mentoringPostRepository.findByIdOrElseThrow(mentoringPostId);

		MentoringPostDetailResponseDto mentoringPostResponseDto = MentoringPostDetailResponseDto.builder()
			.mentoringPostId(mentoringPost.getId())
			.userNickname(mentoringPost.getUser().getUserNickname())
			.field(mentoringPost.getField())
			.title(mentoringPost.getTitle())
			.company(mentoringPost.getCompany())
			.career(mentoringPost.getCareer())
			.region(mentoringPost.getRegion())
			.price(mentoringPost.getPrice())
			.description(mentoringPost.getDescription())
			.build();
		return CommonResponseDto.<MentoringPostDetailResponseDto>builder().msg("멘토링 공고 상세 조회되었습니다.").data(mentoringPostResponseDto).build();
	}


}
