package com.connect.codeness.domain.review.controller;

import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN;
import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;
import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;

import com.connect.codeness.domain.review.dto.ReviewResponseDetailDto;
import com.connect.codeness.domain.review.service.ReviewService;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

	private final ReviewService reviewService;
	private final JwtProvider jwtProvider;

	public ReviewController(ReviewService reviewService, JwtProvider jwtProvider) {
		this.reviewService = reviewService;
		this.jwtProvider = jwtProvider;
	}

	@PostMapping("/payment-history/{paymentHistoryId}/reviews")
	public ResponseEntity<CommonResponseDto> createReview(
		@PathVariable Long paymentHistoryId,
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@Valid @RequestBody ReviewCreateRequestDto dto
	) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);
		CommonResponseDto commonResponseDto = reviewService.createReview(userId, paymentHistoryId,
			dto);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED);
	}

	@GetMapping("/mentoring/{mentoringPostId}/reviews")
	public ResponseEntity<CommonResponseDto<PaginationResponseDto<ReviewResponseDto>>> findReviews(
		@PathVariable Long mentoringPostId,
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize
	) {
		CommonResponseDto<PaginationResponseDto<ReviewResponseDto>> commonResponseDto
			= reviewService.findReviews(mentoringPostId, pageNumber, pageSize);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	@GetMapping("/payment-history/{paymentHistoryId}/reviews")
	public ResponseEntity<CommonResponseDto<ReviewResponseDetailDto>> findReview(
		@PathVariable Long paymentHistoryId,
		@RequestHeader(AUTHORIZATION) String authorizationHeader
	) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);

		CommonResponseDto<ReviewResponseDetailDto> commonResponseDto
			= reviewService.findReview(userId, paymentHistoryId);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	@DeleteMapping("/reviews/{reviewId}")
	public ResponseEntity<CommonResponseDto> deleteReview(
		@PathVariable Long reviewId,
		@RequestHeader(AUTHORIZATION) String authorizationHeader
	) {
		Long userId = jwtProvider.extractUserId(authorizationHeader);
		CommonResponseDto commonResponseDto = reviewService.deleteReview(userId, reviewId);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}
}
