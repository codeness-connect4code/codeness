package com.connect.codeness.domain.review;

import com.connect.codeness.domain.paymentlist.PaymentList;
import com.connect.codeness.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
	@JoinColumn(name = "payment_list_id")
	@NotNull
	private PaymentList paymentList;

	@NotNull
	@Min(1)
	@Max(5)
	private Integer starRating;

	@NotBlank
	@Size(max = 300)
	private String reviewContent;

	public Review(){ }

	@Builder
	public Review(PaymentList paymentList, Integer starRating,
		String reviewContent) {
		this.paymentList = paymentList;
		this.starRating = starRating;
		this.reviewContent = reviewContent;
	}
}
