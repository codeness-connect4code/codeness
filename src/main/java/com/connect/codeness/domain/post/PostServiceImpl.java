package com.connect.codeness.domain.post;

import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.domain.post.dto.PostResearchResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.CommunityStatus;
import com.connect.codeness.global.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

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
			.writer(user.getUserNickname())
			.view(0l)
			.postType(dto.getPostType())
			.status(CommunityStatus.DISPLAYED)
			.build();

		// 객체 저장
		postRepository.save(post);

		// 성공메세지 반환
		return CommonResponseDto.builder()
			.msg("게시글 작성이 완료되었습니다.")
			.build();
	}

	// 게시글 조회 메서드
	@Override
	public CommonResponseDto<Page<PostResearchResponseDto>> findAllPost(PostType postType, String keyword, String writer, Pageable pageable) {

		// DB 에서 게시글 가져오기
		Page<Post> posts = postRepository.findByTypeAndKeyword(postType, keyword, writer, pageable);

		// DTO로 매핑
		Page<PostResearchResponseDto> postList = posts.map(post -> PostResearchResponseDto.builder()
				.postId(post.getId())
				.title(post.getTitle())
				.writer(post.getWriter())
				.view(post.getView())
				.createdAt(post.getCreatedAt())
				.build());

		// CommonResponseDto에 Page를 직접 포함
		return CommonResponseDto.<Page<PostResearchResponseDto>>builder()
			.msg("게시글 조회가 완료되었습니다.")
			.data(postList)
			.build();
	}
}

