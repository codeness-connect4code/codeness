package com.connect.codeness.domain.mentoringschedule.controller;


import com.connect.codeness.domain.mentoringschedule.dto.MentoringScheduleResponseDto;
import com.connect.codeness.domain.mentoringschedule.service.MentoringScheduleService;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MentoringScheduleController {
	private final MentoringScheduleService mentoringScheduleService;

	public MentoringScheduleController(MentoringScheduleService mentoringScheduleService) {
		this.mentoringScheduleService = mentoringScheduleService;
	}

	/**
	 * 멘토링 공고 스케쥴 전체 조회 API
	 * - 삭제된 공고의 스케쥴은 조회 x
	 */
	@GetMapping("/mentoring/{mentoringPostId}/mentoring-schedule")
	public ResponseEntity<CommonResponseDto<List<MentoringScheduleResponseDto>>> getMentoringSchedule(@PathVariable Long mentoringPostId){

		CommonResponseDto<List<MentoringScheduleResponseDto>> responseDto = mentoringScheduleService.getMentoringSchedule(mentoringPostId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 유효한 멘토링 공고 스케쥴 조회 API
	 * - 상태가 EMPTY & 현재 날짜, 현재 시간 이후 스케쥴만 조회
	 * - 멘토링 신청 페이지에서 호출됨
	 */
	@GetMapping("/mentoring/{mentoringPostId}/mentoring-schedule/empty-status")
	public ResponseEntity<CommonResponseDto<List<MentoringScheduleResponseDto>>> findMentoringSchedulesByEmptyStatus(@PathVariable Long mentoringPostId){

		CommonResponseDto<List<MentoringScheduleResponseDto>> responseDto = mentoringScheduleService.findMentoringSchedulesByEmptyStatus(mentoringPostId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

}
