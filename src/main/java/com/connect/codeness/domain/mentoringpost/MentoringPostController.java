package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mentoring")
public class MentoringPostController {

	private final MentoringPostService mentoringPostService;

	public MentoringPostController(MentoringPostService mentoringPostService) {
		this.mentoringPostService = mentoringPostService;
	}

	/**
	 * 멘토링 공고 생성 API
	 */
	@PostMapping
	public ResponseEntity<CommonResponseDto> createMentoringPost(
		@Valid @RequestBody MentoringPostCreateRequestDto requestDto) {

		CommonResponseDto responseDto = mentoringPostService.createMentoringPost(2L, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}
}
