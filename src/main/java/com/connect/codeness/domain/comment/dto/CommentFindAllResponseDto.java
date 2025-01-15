package com.connect.codeness.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentFindAllResponseDto {

	private Long postId;

	private Long commentId;

	private String content;

	private String writer;

	private String writerProfileUrl;

	private LocalDateTime createdAt;
}