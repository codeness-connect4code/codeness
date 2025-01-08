package com.connect.codeness.domain.post;

import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.CommunityStatus;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

	private PostRepository postRepository;

	public PostServiceImpl(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	// 게시글 생성 메서드
	@Override
	public CommonResponseDto createPost(PostCreateRequestDto dto){

		/*
		* 게시글 객체 생성
		* todo. 토큰에서 유저 객체 받아와서 넣어줘야함.
		*/
		Post post = new Post().builder()
			.title(dto.getTitle())
			.content(dto.getContent())
			.view(0l)
			.type(dto.getPostType())
			.status(CommunityStatus.DISPLAYED)
			.build();

		// 객체 저장
		postRepository.save(post);

		// 성공메세지 반환
		return CommonResponseDto.builder()
			.msg("게시글 작성이 완료되었습니다.")
			.build();
	}
}

