package com.connect.codeness.domain.review.entity;

import com.connect.codeness.domain.mentoringpost.entity.MentoringPost;
import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.ReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "payment_history_id")
	@NotNull
	private PaymentHistory paymentHistory;

	@ManyToOne
	@JoinColumn(name = "mentoring_post_id")
	@NotNull
	private MentoringPost mentoringPost;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Min(1)
	@Max(5)
	@Column(nullable = false, length = 5)
	private Integer starRating;

	@NotBlank
	@Size(max = 300)
	@Column(nullable = false, length = 300)
	private String reviewContent;

	@Column(nullable = false)
	private boolean isDeleted;

	public Review() {
	}

	@Builder
	public Review(PaymentHistory paymentHistory,MentoringPost mentoringPost, User user, Integer starRating,
		String reviewContent) {
		this.paymentHistory = paymentHistory;
		this.mentoringPost = mentoringPost;
		this.user = user;
		this.starRating = starRating;
		this.reviewContent = reviewContent;
		this.isDeleted = false;
	}

	public void delete(){
		this.isDeleted = true;
	}
}
