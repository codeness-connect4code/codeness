package com.connect.codeness.domain.post.dto;

import com.connect.codeness.global.enums.PostType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostFindResponseDto {

	private Long postId;

	private Long userId;

	private String writer;

	private String writerProfileUrl;

	private String title;

	private String content;

	private Long view;

	private PostType postType;

	private LocalDateTime modifiedAt;

	public void inputWriterProfileUrl(String writerProfileUrl){
		this.writerProfileUrl = writerProfileUrl;
	}
}
