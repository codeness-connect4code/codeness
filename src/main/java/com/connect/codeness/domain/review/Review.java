package com.connect.codeness.domain.review;

import com.connect.codeness.domain.mentoringpost.MentoringPost;
import com.connect.codeness.domain.paymentlist.PaymentList;
import com.connect.codeness.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;

@Getter
@Entity
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "payment_list_id")
	private PaymentList paymentList;

	@ManyToOne
	@JoinColumn(name = "mentoring_post_id")
	private MentoringPost mentoringPost;

	private Integer starRating;

	private String reviewContent;

	public Review(){ }

	public Review(PaymentList paymentList, MentoringPost mentoringPost, Integer starRating,
		String reviewContent) {
		this.paymentList = paymentList;
		this.mentoringPost = mentoringPost;
		this.starRating = starRating;
		this.reviewContent = reviewContent;
	}
}
