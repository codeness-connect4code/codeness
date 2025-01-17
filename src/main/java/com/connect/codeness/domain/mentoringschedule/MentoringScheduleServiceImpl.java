package com.connect.codeness.domain.mentoringschedule;

import com.connect.codeness.domain.mentoringschedule.dto.MentoringScheduleResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MentoringScheduleServiceImpl implements MentoringScheduleService {

	private final MentoringScheduleRepository mentoringScheduleRepository;

	public MentoringScheduleServiceImpl(MentoringScheduleRepository mentoringScheduleRepository) {
		this.mentoringScheduleRepository = mentoringScheduleRepository;
	}

	/**
	 * 멘토링 공고 스케쥴 전체 조회 메서드
	 */
	@Override
	public CommonResponseDto<List<MentoringScheduleResponseDto>> getMentoringSchedule(Long mentoringPostId) {

		//멘토링 공고 스케쥴 list 조회
		List<MentoringSchedule> mentoringSchedule = mentoringScheduleRepository.findByMentoringPostId(mentoringPostId);

		//멘토링 공고 존재하지 않으면 예외처리


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

		return CommonResponseDto.<List<MentoringScheduleResponseDto>>builder().msg("멘토링 공고 스케쥴이 조회되었습니다.").data(mentoringScheduleResponseDto).build();
	}

	/**
	 * 유효한 멘토링 공고 스케쥴 조회 API
	 * - TODO : 여기서 스케쥴 검증이 들어가야 한다
	 */


}

