package com.connect.codeness.domain.mentoringpost.controller;

import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;
import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostDetailResponseDto;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostSearchResponseDto;
import com.connect.codeness.domain.mentoringpost.dto.MyMentoringPostResponseDto;
import com.connect.codeness.domain.mentoringpost.service.MentoringPostService;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.global.jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
	@PostMapping("/mentoring")
	public ResponseEntity<CommonResponseDto> createMentoringPost(@RequestHeader(AUTHORIZATION) String token,
		@Valid @RequestBody MentoringPostCreateRequestDto requestDto) {
		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto responseDto = mentoringPostService.createMentoringPost(userId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	/**
	 * 멘토링 공고 삭제 API
	 * - 자신이 생성한 공고만 삭제 가능
	 * - 삭제시 상태 DELETED 변경
	 */
	@PatchMapping("/mentoring/{mentoringPostId}")
	public ResponseEntity<CommonResponseDto> deleteMentoringPost(@RequestHeader(AUTHORIZATION) String token,
		@PathVariable Long mentoringPostId) {
		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto responseDto = mentoringPostService.deleteMentoringPost(userId, mentoringPostId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 멘토링 공고 전체 조회 API
	 * - 모든 유저 가능
	 * - 상태가 존재인 것만 조회
	 */
	@GetMapping("/mentoring")
	public ResponseEntity<CommonResponseDto<PaginationResponseDto<MentoringPostSearchResponseDto>>> getMentoringPostAll(
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize,
		@RequestParam(required = false) String title,
		@RequestParam(required = false) String field,
		@RequestParam(required = false) String nickname) {

		CommonResponseDto<PaginationResponseDto<MentoringPostSearchResponseDto>> responseDto = mentoringPostService.searchMentoringPosts(
			pageNumber, pageSize, title, field, nickname);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 멘토링 공고 상세 조회 API
	 * - 모든 유저 가능
	 * - 상태가 존재인 것만 조회
	 */
	@GetMapping("/mentoring/{mentoringPostId}")
	public ResponseEntity<CommonResponseDto<MentoringPostDetailResponseDto>> getMentoringPostDetail(@PathVariable Long mentoringPostId) {

		CommonResponseDto<MentoringPostDetailResponseDto> responseDto = mentoringPostService.getMentoringPostDetail(mentoringPostId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}


	/**
	 * 멘토가 생성한 멘토링 공고 조회 API
	 * - 멘토가 생성한 멘토링 공고 단건 조회
	 */
	@GetMapping("/mentors/mentoring")
	public ResponseEntity<CommonResponseDto<MyMentoringPostResponseDto>> findMentoringPostByMentorId(@RequestHeader(AUTHORIZATION) String token) {

		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto<MyMentoringPostResponseDto> responseDto = mentoringPostService.findMentoringPostByMentorId(userId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 멘티가 결제한 멘토링 공고 조회 API
	 * - 멘티가 결제한 스케쥴의 멘토링 공고를 조회
	 * - 중복된 멘토링 공고일 경우 1개만 조회
	 */
	@GetMapping("/mentees/mentoring")
	public ResponseEntity<CommonResponseDto<PaginationResponseDto<MyMentoringPostResponseDto>>> findMentoringPostByMenteeId(
		@RequestHeader(AUTHORIZATION) String token,
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize) {

		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto<PaginationResponseDto<MyMentoringPostResponseDto>> responseDto = mentoringPostService.findMentoringPostByMenteeId(userId, pageNumber, pageSize);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

}
