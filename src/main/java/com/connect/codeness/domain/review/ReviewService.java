package com.connect.codeness.domain.review;


import com.connect.codeness.domain.mentoringpost.dto.PaginationResponseDto;
import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewFindResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface ReviewService {

	CommonResponseDto createReview(Long userId, Long paymentHistoryId, ReviewCreateRequestDto dto);

	CommonResponseDto<PaginationResponseDto<ReviewFindResponseDto>> findReviews(Long mentoringPostId, int pageNumber,
		int pageSize);

	CommonResponseDto deleteReview(Long userId, Long reviewId);
}

