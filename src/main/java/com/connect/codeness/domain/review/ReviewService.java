package com.connect.codeness.domain.review;


import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface ReviewService {
    CommonResponseDto createReview(Long paymentListId, ReviewCreateRequestDto dto);
}

