package com.connect.codeness.domain.post.dto;

import com.connect.codeness.global.enums.PostType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostFindResponseDto {

	private Long postId;

	private String writer;

	private String writerProfileUrl;

	private String title;

	private String content;

	private Long view;

	private PostType postType;
}
