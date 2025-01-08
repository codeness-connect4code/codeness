package com.connect.codeness.domain.mentorrequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class MentorRequestController {

	private final MentorRequestService mentorRequestService;

	public MentorRequestController(MentorRequestService mentorRequestService) {
		this.mentorRequestService = mentorRequestService;
	}

//	//todo : 파일 service 구현시 파일 추가로 수정 (사원증 이미지 첨부)
//	@PostMapping("/mentors")
//	public ResponseEntity<CommonResponseDto> createMentoringPost(
//		@RequestBody MentoringPostCreateDto mentoringPostCreateDto
//	) {}

}
