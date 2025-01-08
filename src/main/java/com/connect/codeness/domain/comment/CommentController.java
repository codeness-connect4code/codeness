package com.connect.codeness.domain.comment;

import com.connect.codeness.domain.comment.dto.CommentCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("posts/{postId}/comments")
public class CommentController {

	private final CommentService commentService;

	public CommentController(final CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping
	public ResponseEntity<CommonResponseDto> createComment(@PathVariable("postId") Long postId,
		@Valid @RequestBody CommentCreateRequestDto dto){

		CommonResponseDto responseDto = commentService.createComment(postId, dto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}
}
