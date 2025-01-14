package com.connect.codeness.domain.mentorrequest;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateResponseDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class MentorRequestController {

	private final MentorRequestService mentorRequestService;
	private final JwtUtil jwtUtil;

	public MentorRequestController(MentorRequestService mentorRequestService, JwtUtil jwtUtil) {
		this.mentorRequestService = mentorRequestService;
		this.jwtUtil = jwtUtil;
	}

	//todo : 파일 service 구현시 파일 추가로 수정 (사원증 이미지 첨부), token 구현시 사용자 정보 받아오기 수정(현재는 임의의 userid 1 전송)
	@PostMapping("/mentors")
	public ResponseEntity<CommonResponseDto> createMentorRequest(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@Valid @ModelAttribute MentorRequestCreateResponseDto mentorRequestCreateResponseDto
	) {
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);

		CommonResponseDto response = mentorRequestService.createMentorRequest(
			tokenId, mentorRequestCreateResponseDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}