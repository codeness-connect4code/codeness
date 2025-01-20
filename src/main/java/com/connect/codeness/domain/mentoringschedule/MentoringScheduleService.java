package com.connect.codeness.domain.mentoringschedule;


import com.connect.codeness.domain.mentoringschedule.dto.MentoringScheduleResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;

public interface MentoringScheduleService {

	/**
	 * 멘토링 공고 스케쥴 전체 조회 메서드
	 */
	CommonResponseDto<List<MentoringScheduleResponseDto>> getMentoringSchedule(Long mentoringPostId);

	/**
	 * 유효한 멘토링 공고 스케쥴 조회 API
	 */
	CommonResponseDto<List<MentoringScheduleResponseDto>> findMentoringSchedulesByEmptyStatus(Long mentoringPostId);

}

