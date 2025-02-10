package com.connect.codeness.domain.review.service;


import com.connect.codeness.domain.review.dto.ReviewResponseDetailDto;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface ReviewService {

	CommonResponseDto<?> createReview(Long userId, Long paymentHistoryId, ReviewCreateRequestDto dto);

	CommonResponseDto<PaginationResponseDto<ReviewResponseDto>> findReviews(Long mentoringPostId, int pageNumber,
		int pageSize);

	CommonResponseDto<?> deleteReview(Long userId, Long reviewId);

	CommonResponseDto<ReviewResponseDetailDto> findReview(Long userId, Long paymentHistoryId);
}

