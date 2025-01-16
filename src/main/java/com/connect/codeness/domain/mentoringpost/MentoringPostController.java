package com.connect.codeness.domain.mentoringpost;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.global.jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mentoring")
public class MentoringPostController {

	private final MentoringPostService mentoringPostService;
	private final JwtUtil jwtUtil;

	public MentoringPostController(MentoringPostService mentoringPostService, JwtUtil jwtUtil) {
		this.mentoringPostService = mentoringPostService;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * 멘토링 공고 생성 API
	 * - 멘토만 가능
	 */
	@PostMapping
	public ResponseEntity<CommonResponseDto> createMentoringPost(@RequestHeader(AUTHORIZATION) String token, @Valid @RequestBody MentoringPostCreateRequestDto requestDto) {
		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto responseDto = mentoringPostService.createMentoringPost(userId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	/**
	 * 멘토링 공고 삭제 API
	 * - 자신이 생성한 공고만 삭제 가능
	 */
	@DeleteMapping("/{mentoringPostId}")
	public ResponseEntity<CommonResponseDto> deleteMentoringPost(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long mentoringPostId){
		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto responseDto = mentoringPostService.deleteMentoringPost(userId, mentoringPostId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
