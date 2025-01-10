package com.connect.codeness.domain.post.dto;

import com.connect.codeness.global.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateRequestDto {

	@NotBlank(message = "제목을 입력해주세요.")
	@Size(max = 50, message = "제목은 최대 50자 입니다.")
	private String title;

	@NotBlank(message = "내용은 비워둘 수 없습니다.")
	private String content;

	@NotNull(message = "게시글 타입을 설정해주세요.")
	private PostType postType;
}
