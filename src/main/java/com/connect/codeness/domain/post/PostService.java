package com.connect.codeness.domain.post;


import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.domain.post.dto.PostResearchResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

	CommonResponseDto createPost(Long userId, PostCreateRequestDto postCreateRequestDto);

	CommonResponseDto<Page<PostResearchResponseDto>> findAllPost(PostType postType, String keyword, String writer, Pageable pageable);
}

