package com.connect.codeness.domain.review;

import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import com.connect.codeness.domain.review.dto.ReviewFindResponseDto;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
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
		    SELECT new com.connect.codeness.domain.review.dto.ReviewFindResponseDto(
		    r.id, r.user.id, r.reviewContent, r.starRating, r.createdAt)
		    FROM Review r
		    WHERE r.mentoringPost.id = :postId
		""")
	Page<ReviewFindResponseDto> findByMentoringPostId(@Param("postId") Long postId, Pageable pageable);

	Boolean existsByPaymentHistory(PaymentHistory paymentHistory);
}
