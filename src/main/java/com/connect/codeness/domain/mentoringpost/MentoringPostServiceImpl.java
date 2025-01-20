package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostDetailResponseDto;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostSearchResponseDto;
import com.connect.codeness.domain.review.ReviewRepository;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.mentoringschedule.MentoringSchedule;
import com.connect.codeness.domain.mentoringschedule.MentoringScheduleRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.BookedStatus;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.MentoringPostStatus;
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
import java.util.stream.Stream;
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
	private final ReviewRepository reviewRepository;

	public MentoringPostServiceImpl(MentoringPostRepository mentoringPostRepository,
		UserRepository userRepository, MentoringScheduleRepository mentoringScheduleRepository, ReviewRepository reviewRepository) {
		this.mentoringPostRepository = mentoringPostRepository;
		this.userRepository = userRepository;
		this.mentoringScheduleRepository = mentoringScheduleRepository;
		this.reviewRepository = reviewRepository;
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

		//로그인한 유저가 멘토 공고를 생성했고, 상태가 DISPLAYED면 예외
		if(mentoringPostRepository.findMentoringPostStatusByUserId(user.getId())){
			throw new BusinessException(ExceptionType.MENTORING_POST_CREATE_NOT_ALLOWED);
		}

		//현재 날짜 & 시간 가져오기
		LocalDateTime now = LocalDateTime.now();

		//시작 날짜와 시간 검증하기
		LocalDateTime startDateTime = LocalDateTime.of(requestDto.getStartDate(), requestDto.getStartTime());
		if(startDateTime.isBefore(now)){
			throw new BusinessException(ExceptionType.INVALID_START_DATE_TIME);
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
			.mentoringPostStatus(MentoringPostStatus.DISPLAYED)
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
	 * - 시작 날짜, 종료 날짜, 시작 시간, 종료 시간을 기반으로 멘토링 스케쥴 생성
	 */
	private List<MentoringSchedule> createMentoringSchedules(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
		MentoringPost mentoringPost) {

		return startDate.datesUntil(endDate.plusDays(1)) //시작 날짜 - 종료 날짜 범위 생성
			.flatMap(date -> createSchedulesForDate(date, startTime, endTime, mentoringPost))
			.collect(Collectors.toList());
	}

	/**
	 * 멘토링 공고 - 멘토링 스케쥴 생성 서비스 메서드
	 * - 시간 범위가 하루를 넘어가서 날짜가 바뀌는 경우 처리
	 */
	private Stream<MentoringSchedule> createSchedulesForDate(LocalDate date, LocalTime startTime, LocalTime endTime,
		MentoringPost mentoringPost) {

		boolean crossesMidnight = endTime.isBefore(startTime);
		//시작 시간과 종료 시간 사이의 총 시간 개수를 계산
		long hourCount = crossesMidnight
			? ChronoUnit.HOURS.between(startTime, LocalTime.MAX) + 1 + ChronoUnit.HOURS.between(LocalTime.MIN, endTime.plusHours(1))
			: ChronoUnit.HOURS.between(startTime, endTime.plusHours(1));

		return LongStream.range(0, hourCount)
			.mapToObj(hour -> buildMentoringSchedule(date, startTime.plusHours(hour), mentoringPost));//시간별 스케쥴 생성
	}

	/**
	 * 멘토링 공고 - 멘토링 스케쥴 생성 서비스 메서드
	 * - 멘토링 스케쥴 단건 생성
	 */
	private MentoringSchedule buildMentoringSchedule(LocalDate date, LocalTime time, MentoringPost mentoringPost) {

		// 날짜 & 시간 합쳐서 LocalDateTime 생성
		LocalDateTime dateTime = date.atTime(time); 

		//날짜 넘어가는 경우
		if (dateTime.toLocalTime().isBefore(time)) {
			dateTime = dateTime.plusDays(1); // 다음 날로 조정
		}

		// MentoringSchedule 생성, 반환
		return MentoringSchedule.builder()
			.mentoringPost(mentoringPost)
			.mentoringDate(dateTime.toLocalDate())
			.mentoringTime(dateTime.toLocalTime())
			.bookedStatus(BookedStatus.EMPTY)
			.build();
	}

	/**
	 * 멘토링 공고 삭제 서비스 메서드
	 * - 삭제하면 상태 DELETE 변경
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

		//이미 공고가 삭제 상태라면
		if(mentoringPost.getMentoringPostStatus().equals(MentoringPostStatus.DELETED)){
			throw new BusinessException(ExceptionType.MENTORING_POST_DELETED);
		}

		//멘토링 공고 삭제로 상태 업데이트
		mentoringPost.updateStatus(MentoringPostStatus.DELETED);

		return CommonResponseDto.builder().msg("멘토링 공고가 삭제되었습니다.").build();
	}

	/**
	 * 멘토링 공고 전체 조회 서비스 메서드
	 * - 모든 유저 가능
	 * - DISPLAYED 상태만 조회
	 */
	@Override
	public CommonResponseDto<PaginationResponseDto<MentoringPostSearchResponseDto>> searchMentoringPosts(
		int pageNumber, int pageSize, String title, String field, String nickname) {
		//페이징
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		//멘토링 공고 조회
		Page<MentoringPostSearchResponseDto> mentoringPosts = mentoringPostRepository.findAllBySearchParameters(title, field, nickname,
			pageable);

		//PaginationResponseDto 생성
		PaginationResponseDto<MentoringPostSearchResponseDto> paginationResponseDto = PaginationResponseDto.<MentoringPostSearchResponseDto>builder()
			.content(mentoringPosts.getContent())
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
	 * - 모든 유저 가능
	 * - DISPLAYED 상태만 조회
	 */
	@Override
	public CommonResponseDto<MentoringPostDetailResponseDto> getMentoringPostDetail(Long mentoringPostId) {
		//멘토링 공고 조회
		MentoringPost mentoringPost = mentoringPostRepository.findByIdOrElseThrow(mentoringPostId);
		
		//멘토링 공고가 삭제 상태이면
		if(mentoringPost.getMentoringPostStatus().equals(MentoringPostStatus.DELETED)){
			throw new BusinessException(ExceptionType.MENTORING_POST_DELETED);
		}

		//평균 리뷰 조회
		Double mentoringPostStarRating = reviewRepository.findAverageStarRatingByMentoringPostId(mentoringPostId);

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
			.starRating(mentoringPostStarRating)
			.build();
		return CommonResponseDto.<MentoringPostDetailResponseDto>builder().msg("멘토링 공고 상세 조회되었습니다.").data(mentoringPostResponseDto).build();
	}


}
