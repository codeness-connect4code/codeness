package com.connect.codeness.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCreateRequestDto {

	@NotBlank
	@Size(max = 300, message = "리뷰 내용은 최대 300자입니다.")
	private final String content;

	@NotNull
	@Min(value = 1, message = "최소 1개의 별점을 줄 수 있습니다.")
	@Max(value = 5, message = "최대 5개의 별점을 줄 수 있습니다.")
	private final Integer starRating;
}
