package com.connect.codeness.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostUpdateRequestDto {

	@NotBlank(message = "제목을 입력해주세요.")
	@Size(max = 50, message = "제목은 최대 50자 입니다.")
	private String title;

	@NotBlank(message = "내용은 비워둘 수 없습니다.")
	private String content;
}