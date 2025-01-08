package com.connect.codeness.domain.review;

import com.connect.codeness.domain.mentoringschedule.MentoringSchedule;
import com.connect.codeness.domain.paymentlist.PaymentList;
import com.connect.codeness.domain.paymentlist.PaymentListRepository;
import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewFindResponseDto;
import com.connect.codeness.global.constants.Constants;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        //멘토링 날짜가 아직 아니라면
        MentoringSchedule mentoringSchedule = paymentList.getPayment().getMentoringSchedule();

        LocalDate mentoringDate = mentoringSchedule.getMentoringDate();

        LocalDate nowDate = LocalDate.now();
        if(mentoringDate.isAfter(nowDate)){
            throw new BusinessException(ExceptionType.TOO_EARLY_REVIEW);
        }

        //멘토링 시간이 아직이라면
        LocalTime mentoringTime = mentoringSchedule.getMentoringTime();

        LocalTime nowTime = LocalTime.now();

        if(mentoringTime.isAfter(nowTime)){
            throw new BusinessException(ExceptionType.TOO_EARLY_REVIEW);
        }

        Review review = Review.builder()
            .paymentList(paymentList)
            .reviewContent(dto.getContent())
            .starRating(dto.getStarRating())
            .build();

        reviewRepository.save(review);

        return CommonResponseDto.builder().msg("리뷰 생성이 완료되었습니다.").build();
    }

    @Override
    public CommonResponseDto<Page<ReviewFindResponseDto>> findReviews(Long mentoringPostId, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, Constants.PAGE_SIZE, Sort.by("createdAt").descending());

        Page<Review> reviews = reviewRepository.findByMentoringPostId(mentoringPostId, pageable);

        Page<ReviewFindResponseDto> responseDto = reviews.map(review -> ReviewFindResponseDto.builder()
            .reviewId(review.getId())
            .content(review.getReviewContent())
            .starRating(review.getStarRating())
            .createdAt(review.getCreatedAt())
            .build());

        return CommonResponseDto.<Page<ReviewFindResponseDto>>builder()
            .msg("리뷰가 조회되었습니다.")
            .data(responseDto)
            .build();
    }

    @Override
    public CommonResponseDto deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findByIdOrElseThrow(reviewId);

        //내가 작성한 리뷰가 아니면 삭제 못함!
        if(review.getPaymentList().getUser().getId() != userId){
            throw new BusinessException(ExceptionType.UNAUTHORIZED_DELETE_REQUEST);
        }

        reviewRepository.delete(review);

        return CommonResponseDto.builder().msg("리뷰 삭제가 완료되었습니다.").build();
    }
}

