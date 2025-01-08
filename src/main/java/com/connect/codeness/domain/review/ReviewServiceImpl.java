package com.connect.codeness.domain.review;

import com.connect.codeness.domain.paymentlist.PaymentList;
import com.connect.codeness.domain.paymentlist.PaymentListRepository;
import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PaymentListRepository paymentListRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
        PaymentListRepository paymentListRepository) {

        this.reviewRepository = reviewRepository;
        this.paymentListRepository = paymentListRepository;
    }

    @Override
    public CommonResponseDto createReview(Long paymentListId, ReviewCreateRequestDto dto) {
        PaymentList paymentList = paymentListRepository.findById(paymentListId).orElseThrow(
            () -> new BusinessException(ExceptionType.NOT_FOUND_PAYMENTLIST)
        );

        Review review = Review.builder()
            .paymentList(paymentList)
            .reviewContent(dto.getContent())
            .starRating(dto.getStarRating())
            .build();

        reviewRepository.save(review);

        return CommonResponseDto.builder().msg("리뷰 생성이 완료되었습니다.").build();
    }
}

