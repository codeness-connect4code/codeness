package com.connect.codeness.domain.review.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDetailDto {

	private Long reviewId;

	private String profileUrl;

	private String mentorNick;

	private String mentoringTitle;

	private String content;

	private Integer starRating;

	private LocalDateTime createdAt;
}
