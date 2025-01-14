package com.connect.codeness.domain.review;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;
import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;

import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewFindResponseDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
	private final JwtUtil jwtUtil;

	public ReviewController(ReviewService reviewService, JwtUtil jwtUtil) {
		this.reviewService = reviewService;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/payment-history/{paymentHistoryId}/reviews")
	public ResponseEntity<CommonResponseDto> createReview(
		@PathVariable Long paymentHistoryId,
		@RequestHeader(AUTHORIZATION) String token,
		@Valid @RequestBody ReviewCreateRequestDto dto
	) {
		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto commonResponseDto = reviewService.createReview(userId, paymentHistoryId,
			dto);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED);
	}

	@GetMapping("/mentoring-posts/{mentoringPostId}/reviews")
	public ResponseEntity<CommonResponseDto<Page<ReviewFindResponseDto>>> findReviews(
		@PathVariable Long mentoringPostId,
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize
	) {
		CommonResponseDto<Page<ReviewFindResponseDto>> commonResponseDto
			= reviewService.findReviews(mentoringPostId, pageNumber, pageSize);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	@DeleteMapping("/reviews/{reviewId}")
	public ResponseEntity<CommonResponseDto> deleteReview(
		@PathVariable Long reviewId,
		@RequestHeader(AUTHORIZATION) String token
	) {
		Long userId = jwtUtil.extractUserId(token);
		CommonResponseDto commonResponseDto = reviewService.deleteReview(userId, reviewId);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}
}
