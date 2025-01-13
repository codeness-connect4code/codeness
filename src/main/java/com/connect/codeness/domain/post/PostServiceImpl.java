package com.connect.codeness.domain.post;

import com.connect.codeness.domain.file.FileRepository;
import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.domain.post.dto.PostFindAllResponseDto;
import com.connect.codeness.domain.post.dto.PostFindResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.CommunityStatus;
import com.connect.codeness.global.enums.PostType;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
public class PostServiceImpl implements PostService {

	private PostRepository postRepository;
	private UserRepository userRepository;
	private FileRepository fileRepository;

	public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, FileRepository fileRepository) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.fileRepository = fileRepository;
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

	// 게시글 목록 조회
	@Override
	public CommonResponseDto<Page<PostFindAllResponseDto>> findAllPost(PostType postType, String keyword, String writer, Pageable pageable) {

		// DB 에서 게시글 가져오기
		Page<Post> posts = postRepository.findByTypeAndKeyword(postType, keyword, writer, pageable);

		// DTO로 매핑
		Page<PostFindAllResponseDto> postList = posts.map(post -> PostFindAllResponseDto.builder()
				.postId(post.getId())
				.title(post.getTitle())
				.writer(post.getWriter())
				.view(post.getView())
				.createdAt(post.getCreatedAt())
				.build());

		// CommonResponseDto에 Page를 직접 포함
		return CommonResponseDto.<Page<PostFindAllResponseDto>>builder()
			.msg("게시글 목록 조회가 완료되었습니다.")
			.data(postList)
			.build();
	}

	@Override
	public CommonResponseDto<PostFindResponseDto> findPost(Long postId) {

		Post post = postRepository.findByIdOrElseThrow(postId);

		ImageFile writerProfile = fileRepository.findByUserId(post.getUser().getId())
			.orElseThrow(()-> new BusinessException(ExceptionType.NOT_FOUND_USER));

		PostFindResponseDto postFindResult = PostFindResponseDto.builder()
			.postId(post.getId())
			.title(post.getTitle())
			.writer(post.getWriter())
			.view(post.getView())
			.content(post.getContent())
			.writerProfileUrl(writerProfile.getFilePath())
			.postType(post.getPostType())
			.build();

		return CommonResponseDto.<PostFindResponseDto>builder()
			.msg("게시글 상세 조회가 완료되었습니다.")
			.data(postFindResult)
			.build();
	}
}

