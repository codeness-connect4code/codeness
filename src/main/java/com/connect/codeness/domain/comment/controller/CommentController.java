package com.connect.codeness.domain.comment.controller;


import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;
import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;

import com.connect.codeness.domain.comment.dto.CommentCreateRequestDto;
import com.connect.codeness.domain.comment.dto.CommentFindAllResponseDto;
import com.connect.codeness.domain.comment.service.CommentService;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.global.jwt.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping
public class CommentController {

	private final CommentService commentService;
	private final JwtUtil jwtUtil;

	public CommentController(final CommentService commentService, JwtUtil jwtUtil) {
		this.commentService = commentService;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("posts/{postId}/comments")
	public ResponseEntity<CommonResponseDto> createComment(
		@PathVariable("postId") Long postId,
		@Valid @RequestBody CommentCreateRequestDto dto,
		@RequestHeader(AUTHORIZATION) String token){

		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto responseDto = commentService.createComment(postId, userId, dto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	@GetMapping("posts/{postId}/comments")
	public ResponseEntity findAllComment(
		@PathVariable("postId") Long postId,
		@RequestParam(required = false, defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

		CommonResponseDto<PaginationResponseDto<CommentFindAllResponseDto>> responseDto = commentService.findAllComment(postId, pageable);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@PatchMapping("comments/{commentId}")
	public ResponseEntity updateComment(
		@PathVariable("commentId") Long commentId,
		@Valid @RequestBody CommentCreateRequestDto dto,
		@RequestHeader(AUTHORIZATION) String token) {

		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto responseDto = commentService.updateComment(commentId, userId, dto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@DeleteMapping("comments/{commentId}")
	public ResponseEntity deleteComment(
		@PathVariable("commentId") Long commentId,
		@RequestHeader(AUTHORIZATION) String token) {

		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto responseDto = commentService.deleteComment(commentId, userId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
