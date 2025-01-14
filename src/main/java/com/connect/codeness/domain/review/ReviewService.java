package com.connect.codeness.domain.review;


import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewFindResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.data.domain.Page;

public interface ReviewService {

	CommonResponseDto createReview(Long userId, Long paymentListId, ReviewCreateRequestDto dto);

	CommonResponseDto<Page<ReviewFindResponseDto>> findReviews(Long mentoringPostId, int pageNumber,
		int pageSize);

	CommonResponseDto deleteReview(Long userId, Long reviewId);
}

