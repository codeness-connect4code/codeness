package com.connect.codeness.domain.review.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDto {

	private Long reviewId;

	private Long userId;

	private String content;

	private Integer starRating;

	private LocalDateTime createdAt;
}
