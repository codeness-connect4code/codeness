package com.connect.codeness.domain.post;

import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.CommunityStatus;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

	private PostRepository postRepository;
	private UserRepository userRepository;

	public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}

	// 게시글 생성 메서드
	@Override
	public CommonResponseDto createPost(Long userId, PostCreateRequestDto dto){

		User user = userRepository.findByIdOrElseThrow(userId);

		Post post = new Post().builder()
			.user(user)
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

