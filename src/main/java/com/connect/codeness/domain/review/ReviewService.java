package com.connect.codeness.domain.review;


import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewFindResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.data.domain.Page;

public interface ReviewService {
    CommonResponseDto createReview(Long paymentListId, ReviewCreateRequestDto dto);

    CommonResponseDto<Page<ReviewFindResponseDto>> findReviews(Long mentoringPostId, int pageNumber);

    CommonResponseDto deleteReview(Long reviewId, Long userId);
}

