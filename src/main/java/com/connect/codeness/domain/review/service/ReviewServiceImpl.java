package com.connect.codeness.domain.review.service;

import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.mentoringpost.entity.MentoringPost;
import com.connect.codeness.domain.mentoringpost.repository.MentoringPostRepository;
import com.connect.codeness.domain.review.dto.ReviewResponseDetailDto;
import com.connect.codeness.domain.review.entity.Review;
import com.connect.codeness.domain.review.repository.ReviewRepository;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.mentoringschedule.entity.MentoringSchedule;
import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.domain.paymenthistory.repository.PaymentHistoryRepository;
import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewResponseDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
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
	private final MentoringPostRepository mentoringPostRepository;

	public ReviewServiceImpl(ReviewRepository reviewRepository,
		PaymentHistoryRepository paymentHistoryRepository, MentoringPostRepository mentoringPostRepository) {

		this.reviewRepository = reviewRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
		this.mentoringPostRepository = mentoringPostRepository;
	}

	@Transactional
	@Override
	public CommonResponseDto createReview(Long userId, Long paymentHistoryId,
		ReviewCreateRequestDto dto) {
		//리뷰를 생성할 거래 내역 가져오기
		PaymentHistory paymentHistory = paymentHistoryRepository.findByIdOrElseThrow(paymentHistoryId);

		//내가 거래한 내역이 아니라면 생성 x
		User user = paymentHistory.getPayment().getUser();

		if (!Objects.equals(user.getId(), userId)) {
			throw new BusinessException(ExceptionType.UNAUTHORIZED_POST_REQUEST);
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

		//멘토링 공고 가져오기
		MentoringPost mentoringPost = mentoringPostRepository.findByIdOrElseThrow(dto.getMentoringPostId());

		Review review = Review.builder()
			.paymentHistory(paymentHistory)
			.mentoringPost(mentoringPost)
			.user(user)
			.reviewContent(dto.getContent())
			.starRating(dto.getStarRating())
			.build();

		//후기 내용 작성 후 결제내역의 후기 작성 상태 COMPLETE
		reviewRepository.save(review);
		paymentHistory.updateReviewStatus(ReviewStatus.COMPLETE);

		return CommonResponseDto.builder().msg("리뷰 생성이 완료되었습니다.").build();
	}

	@Override
	public CommonResponseDto<PaginationResponseDto<ReviewResponseDto>> findReviews(Long mentoringPostId,
		int pageNumber, int pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

		Page<ReviewResponseDto> reviews = reviewRepository.findByMentoringPostId(mentoringPostId, pageable);

		PaginationResponseDto<ReviewResponseDto> responseDto = PaginationResponseDto.<ReviewResponseDto>builder()
			.content(reviews.getContent())
			.totalPages(reviews.getTotalPages())
			.totalElements(reviews.getTotalElements())
			.pageNumber(pageNumber)
			.pageSize(pageSize)
			.build();

		return CommonResponseDto.<PaginationResponseDto<ReviewResponseDto>>builder()
			.msg("리뷰가 조회되었습니다.")
			.data(responseDto)
			.build();
	}

	@Transactional
	@Override
	public CommonResponseDto deleteReview(Long userId, Long reviewId) {
		Review review = reviewRepository.findByIdOrElseThrow(reviewId);

		//내가 작성한 리뷰가 아니면 삭제 못함!
		if (!Objects.equals(review.getUser().getId(), userId)) {
			throw new BusinessException(ExceptionType.UNAUTHORIZED_DELETE_REQUEST);
		}

		review.getPaymentHistory().updateReviewStatus(ReviewStatus.NOT_YET);
		review.delete();

		return CommonResponseDto.builder().msg("리뷰 삭제가 완료되었습니다.").build();
	}

	//결제내역에서 리뷰 단건 조회
	@Override
	public CommonResponseDto<ReviewResponseDetailDto> findReview(Long userId, Long paymentHistoryId) {

		PaymentHistory paymentHistory = paymentHistoryRepository.findByIdOrElseThrow(paymentHistoryId);

		//내가 결제한 리뷰가 아니면 조회 못함!
		if(!Objects.equals(paymentHistory.getPayment().getUser().getId(), userId)){
			throw new BusinessException(ExceptionType.UNAUTHORIZED_GET_REQUEST);
		}

		Review review  = reviewRepository.findByPaymentHistoryIdOrElseThrow(paymentHistoryId);

		ImageFile profileFile = review.getMentoringPost().getUser().getImageFiles().stream()
			.filter(imageFile -> imageFile.getFileCategory() == FileCategory.PROFILE).findFirst().orElse(null);

		String profileUrl = null;

		if(profileFile != null){
			profileUrl = profileFile.getFilePath();
		}

		ReviewResponseDetailDto responseDto = ReviewResponseDetailDto.builder()
			.reviewId(review.getId())
			.profileUrl(profileUrl)
			.mentorNick(review.getMentoringPost().getUser().getUserNickname())
			.mentoringTitle(review.getMentoringPost().getTitle())
			.content(review.getReviewContent())
			.starRating(review.getStarRating())
			.createdAt(review.getCreatedAt())
			.build();

		return CommonResponseDto.<ReviewResponseDetailDto>builder()
			.msg("리뷰 상세가 조회되었습니다.")
			.data(responseDto).build();
	}
}

