package com.connect.codeness.domain.post;


import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface PostService {

	CommonResponseDto createPost(PostCreateRequestDto postCreateRequestDto);

}

