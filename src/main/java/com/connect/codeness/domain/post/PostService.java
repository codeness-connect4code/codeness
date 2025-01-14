package com.connect.codeness.domain.post;


import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.domain.post.dto.PostFindAllResponseDto;
import com.connect.codeness.domain.post.dto.PostFindResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

	// 게시글 생성
	CommonResponseDto createPost(Long userId, PostCreateRequestDto postCreateRequestDto);

	// 게시글 목록 조회
	CommonResponseDto<Page<PostFindAllResponseDto>> findAllPost(PostType postType, String keyword, String writer, Pageable pageable);

	// 게시글 상세 조회
	CommonResponseDto<PostFindResponseDto> findPost(Long postId);
}

