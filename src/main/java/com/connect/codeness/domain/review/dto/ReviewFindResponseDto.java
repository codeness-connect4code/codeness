package com.connect.codeness.domain.review.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewFindResponseDto {

    private Long reviewId;

    private String content;

    private Integer starRating;

    private LocalDateTime createdAt;
}
