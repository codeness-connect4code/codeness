package com.connect.codeness.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCreateRequestDto {
    private final String content;
    private final Integer starRating;
}
