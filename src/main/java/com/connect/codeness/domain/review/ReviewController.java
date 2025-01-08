package com.connect.codeness.domain.review;

import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/payment-list/{paymentListId}/reviews")
    public ResponseEntity<CommonResponseDto> createReview(
        @PathVariable Long paymentListId,
        @RequestBody ReviewCreateRequestDto dto
    ){

        CommonResponseDto commonResponseDto = reviewService.createReview(paymentListId, dto);
        hihihihi
        return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED);
    }
}
