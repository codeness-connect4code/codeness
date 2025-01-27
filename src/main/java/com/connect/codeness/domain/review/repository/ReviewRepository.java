package com.connect.codeness.domain.review.repository;

import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.domain.review.dto.ReviewResponseDto;
import com.connect.codeness.domain.review.entity.Review;
import com.connect.codeness.global.enums.ReviewStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	default Review findByIdOrElseThrow(Long reviewId) {
		return findById(reviewId).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_REVIEW)
		);
	}

	@Query("""
		    SELECT new com.connect.codeness.domain.review.dto.ReviewResponseDto(
		    r.id, r.user.id, r.reviewContent, r.starRating, r.createdAt)
		    FROM Review r
		    WHERE r.mentoringPost.id = :postId AND r.reviewStatus = 'COMPLETE'
		""")
	Page<ReviewResponseDto> findByMentoringPostId(@Param("postId") Long postId, Pageable pageable);

	Boolean existsByPaymentHistoryAndReviewStatus(PaymentHistory paymentHistory, ReviewStatus status);

	/**
	 * 평균 별점 조회
	 */
	@Query("""
		SELECT COALESCE(AVG(r.starRating), 0.0)
		FROM Review r
		WHERE  r.mentoringPost.id = :mentoringPostId
	""")
	Double findAverageStarRatingByMentoringPostId(Long mentoringPostId);

	Optional<Review> findByPaymentHistoryIdAndReviewStatus(Long paymentHistoryId, ReviewStatus status);

	default Review findByPaymentHistoryIdOrElseThrow(Long paymentHistoryId, ReviewStatus status){
		Review review = findByPaymentHistoryIdAndReviewStatus(paymentHistoryId, status).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_REVIEW)
		);

		return review;
	}
}
