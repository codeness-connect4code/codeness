package com.connect.codeness.domain.mentoringschedule.service;

import com.connect.codeness.domain.mentoringpost.entity.MentoringPost;
import com.connect.codeness.domain.mentoringpost.repository.MentoringPostRepository;
import com.connect.codeness.domain.mentoringschedule.entity.MentoringSchedule;
import com.connect.codeness.domain.mentoringschedule.repository.MentoringScheduleRepository;
import com.connect.codeness.domain.mentoringschedule.dto.MentoringScheduleResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.BookedStatus;
import com.connect.codeness.global.enums.MentoringPostStatus;
import com.connect.codeness.global.enums.MentoringScheduleStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MentoringScheduleServiceImpl implements MentoringScheduleService {

	private final MentoringScheduleRepository mentoringScheduleRepository;
	private final MentoringPostRepository mentoringPostRepository;

	public MentoringScheduleServiceImpl(MentoringScheduleRepository mentoringScheduleRepository,
		MentoringPostRepository mentoringPostRepository) {
		this.mentoringScheduleRepository = mentoringScheduleRepository;
		this.mentoringPostRepository = mentoringPostRepository;
	}

	/**
	 * 멘토링 공고 스케쥴 전체 조회 메서드
	 * - 상태 & 현재 날짜, 시간은 거르지 않고 조회
	 * - DISPLAYED 상태만 조회 -멘토링 공고 스케쥴 상태 추가 완료 (멘토링 공고가 삭제 상태일 경우 중복되는 데이터가 계속 쌓임)
	 */
	@Override
	public CommonResponseDto<List<MentoringScheduleResponseDto>> getMentoringSchedule(Long mentoringPostId) {
		//멘토링 공고 조회 & 검증
		validatePost(mentoringPostId);

		//멘토링 공고 스케쥴 list 조회
		List<MentoringSchedule> mentoringSchedule = mentoringScheduleRepository.findByMentoringPostId(mentoringPostId);

		//멘토링 공고 스케쥴 -> dto로 변환
		List<MentoringScheduleResponseDto> mentoringScheduleResponseDto = mentoringSchedule.stream()
			.map(mentoringSchedules -> MentoringScheduleResponseDto.builder()
				.id(mentoringSchedules.getId())
				.mentoringPostId(mentoringSchedules.getMentoringPost().getId())
				.mentoringDate(mentoringSchedules.getMentoringDate())
				.mentoringTime(mentoringSchedules.getMentoringTime())
				.bookedStatus(mentoringSchedules.getBookedStatus())
				.build()
			).toList();

		return CommonResponseDto.<List<MentoringScheduleResponseDto>>builder().msg("멘토링 공고 스케쥴이 조회되었습니다.")
			.data(mentoringScheduleResponseDto).build();
	}

	/**
	 * 유효한 멘토링 공고 스케쥴 조회 API
	 * - 상태 empty & 현재 날짜, 시간 이후의 스케쥴
	 * - DISPLAYED 상태만 조회 - 멘토링 공고에서 걸러지긴 함
	 * - TODO : DISPLAY 상태만 조회해야함 - 배치로 날짜 지나면 상태 변하게 바꿈
	 */
	@Override
	public CommonResponseDto<List<MentoringScheduleResponseDto>> findMentoringSchedulesByEmptyStatus(Long mentoringPostId) {
		//멘토링 공고 조회 & 검증
		validatePost(mentoringPostId);

		//현재 날짜 & 시간
		LocalDate currentDate = LocalDate.now();
		LocalTime currentTime = LocalTime.now();

		//유효한 멘토링 공고 스케쥴 list 조회 - 멘토링 스케줄 상태가 DISPLAYED이고, 예약 상태가 EMPTY이며, 현재 날짜 & 시간 이후의 스케쥴만 조회
		List<MentoringSchedule> validMentoringSchedules = mentoringScheduleRepository.findValidMentoringSchedules(mentoringPostId,
			BookedStatus.EMPTY, MentoringScheduleStatus.DISPLAYED, currentDate, currentTime);

		//유효한 스케쥴이 없을 경우
		if (validMentoringSchedules.isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND_MENTORING_SCHEDULE);
		}

		//멘토링 공고 스케쥴 -> dto로 변환
		List<MentoringScheduleResponseDto> mentoringScheduleResponseDto = validMentoringSchedules.stream()
			.map(mentoringSchedules -> MentoringScheduleResponseDto.builder()
				.id(mentoringSchedules.getId())
				.mentoringPostId(mentoringSchedules.getMentoringPost().getId())
				.mentoringDate(mentoringSchedules.getMentoringDate())
				.mentoringTime(mentoringSchedules.getMentoringTime())
				.bookedStatus(mentoringSchedules.getBookedStatus())
				.build()
			).toList();

		return CommonResponseDto.<List<MentoringScheduleResponseDto>>builder().msg("신청이 가능한 멘토링 공고 스케쥴이 조회되었습니다.")
			.data(mentoringScheduleResponseDto).build();
	}

	/**
	 * 멘토링 공고 검증 메서드
	 */
	private void validatePost(Long mentoringPostId) {
		//멘토링 공고 조회
		MentoringPost mentoringPost = mentoringPostRepository.findById(mentoringPostId)
			.orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTORING_POST));

		//멘토링 공고 상태가 삭제면 예외처리
		if (mentoringPost.getMentoringPostStatus().equals(MentoringPostStatus.DELETED)) {
			throw new BusinessException(ExceptionType.MENTORING_POST_DELETED);
		}
	}
}

