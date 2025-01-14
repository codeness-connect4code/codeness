package com.connect.codeness.domain.review;

import com.connect.codeness.domain.mentoringschedule.MentoringSchedule;
import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import com.connect.codeness.domain.paymenthistory.PaymentHistoryRepository;
import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewFindResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.ReviewStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;

	public ReviewServiceImpl(ReviewRepository reviewRepository,
		PaymentHistoryRepository paymentHistoryRepository) {

		this.reviewRepository = reviewRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
	}

	@Transactional
	@Override
	public CommonResponseDto createReview(Long userId, Long paymentHistoryId,
		ReviewCreateRequestDto dto) {
		//리뷰를 생성할 거래 내역 가져오기
		PaymentHistory paymentHistory = paymentHistoryRepository.findByPaymentIdOrElseThrow(paymentHistoryId);

		//내가 거래한 내역이 아니라면 생성 x
		if (!Objects.equals(paymentHistory.getUser().getId(), userId)) {
			throw new BusinessException(ExceptionType.UNAUTHORIZED_CREATE_REQUEST);
		}

		//멘토링 날짜가 아직 아니라면
		MentoringSchedule mentoringSchedule = paymentHistory.getPayment().getMentoringSchedule();

		LocalDate mentoringDate = mentoringSchedule.getMentoringDate();
		LocalTime mentoringTime = mentoringSchedule.getMentoringTime();

		LocalDateTime mentoringDateTime = LocalDateTime.of(mentoringDate, mentoringTime);
		LocalDateTime nowDateTime = LocalDateTime.now();

		if (mentoringDateTime.isAfter(nowDateTime)) {
			throw new BusinessException(ExceptionType.TOO_EARLY_REVIEW);
		}

		Review review = Review.builder()
			.paymentHistory(paymentHistory)
			.reviewContent(dto.getContent())
			.starRating(dto.getStarRating())
			.build();

		//후기 내용 작성 후 결제내역의 후기 작성 상태 COMPLETE
		reviewRepository.save(review);
		paymentHistory.updateReviewStatus(ReviewStatus.COMPLETE);

		return CommonResponseDto.builder().msg("리뷰 생성이 완료되었습니다.").build();
	}

	@Override
	public CommonResponseDto<Page<ReviewFindResponseDto>> findReviews(Long mentoringPostId,
		int pageNumber, int pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

		Page<Review> reviews = reviewRepository.findByMentoringPostId(mentoringPostId, pageable);

		Page<ReviewFindResponseDto> responseDto = reviews.map(
			review -> ReviewFindResponseDto.builder()
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
	public CommonResponseDto deleteReview(Long userId, Long reviewId) {
		Review review = reviewRepository.findByIdOrElseThrow(reviewId);

		//내가 작성한 리뷰가 아니면 삭제 못함!
		if (!Objects.equals(review.getPaymentHistory().getUser().getId(), userId)) {
			throw new BusinessException(ExceptionType.UNAUTHORIZED_DELETE_REQUEST);
		}

		reviewRepository.delete(review);

		return CommonResponseDto.builder().msg("리뷰 삭제가 완료되었습니다.").build();
	}
}

