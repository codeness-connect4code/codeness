package com.connect.codeness.domain.mentoringschedule;


import com.connect.codeness.domain.mentoringschedule.dto.MentoringScheduleResponseDto;
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
	 */
	@GetMapping("/mentoring/{mentoringPostId}/mentoring-schedule")
	public ResponseEntity<CommonResponseDto<List<MentoringScheduleResponseDto>>> getMentoringSchedule(@PathVariable Long mentoringPostId){

		CommonResponseDto<List<MentoringScheduleResponseDto>> responseDto = mentoringScheduleService.getMentoringSchedule(mentoringPostId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * TODO : 유효한 멘토링 공고 스케쥴 조회 API
	 */


}
