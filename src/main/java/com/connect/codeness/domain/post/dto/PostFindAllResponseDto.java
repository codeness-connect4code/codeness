package com.connect.codeness.domain.post.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostFindAllResponseDto {

	private Long postId;

	private String title;

	private String writer;

	private Long view;

	private LocalDateTime createdAt;

}
