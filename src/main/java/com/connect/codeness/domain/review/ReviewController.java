package com.connect.codeness.domain.review;

import com.connect.codeness.domain.review.dto.ReviewCreateRequestDto;
import com.connect.codeness.domain.review.dto.ReviewFindResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
        @Valid @RequestBody ReviewCreateRequestDto dto
    ){

        CommonResponseDto commonResponseDto = reviewService.createReview(paymentListId, dto);

        return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/mentoring-posts/{mentoringPostId}/reviews")
    public ResponseEntity<CommonResponseDto<Page<ReviewFindResponseDto>>> findReviews(
        @PathVariable Long mentoringPostId,
        @RequestParam int pageNumber
    ){
        CommonResponseDto<Page<ReviewFindResponseDto>> commonResponseDto
            = reviewService.findReviews(mentoringPostId, pageNumber);

        return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
    }
}
