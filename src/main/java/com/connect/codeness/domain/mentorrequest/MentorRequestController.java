package com.connect.codeness.domain.mentorrequest;

import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class MentorRequestController {

	private final MentorRequestService mentorRequestService;

	public MentorRequestController(MentorRequestService mentorRequestService) {
		this.mentorRequestService = mentorRequestService;
	}

	//todo : 파일 service 구현시 파일 추가로 수정 (사원증 이미지 첨부), token 구현시 사용자 정보 받아오기 수정(현재는 임의의 userid 1 전송)
	@PostMapping("/mentors")
	public ResponseEntity<CommonResponseDto> createMentorRequest(
		@RequestBody MentorRequestCreateResponseDto mentorRequestCreateResponseDto
	) {
		CommonResponseDto response = mentorRequestService.createMentorRequest(1L, mentorRequestCreateResponseDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
