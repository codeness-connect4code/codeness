package com.connect.codeness.domain.comment.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentCreateRequestDto {

	@Pattern(regexp = "^.{0,100}$", message = "댓글은 최대 100자 입니다.")
	private String content;

}