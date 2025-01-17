package com.connect.codeness.domain.post;

import com.connect.codeness.domain.file.FileRepository;
import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.domain.post.dto.PostFindAllResponseDto;
import com.connect.codeness.domain.post.dto.PostFindResponseDto;
import com.connect.codeness.domain.post.dto.PostUpdateRequestDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.global.enums.PostStatus;
import com.connect.codeness.global.enums.PostType;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import jakarta.transaction.Transactional;
import java.util.Objects;
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

		if (dto.getPostType()==PostType.NOTICE&&user.getRole()!= UserRole.ADMIN){
			throw new BusinessException(ExceptionType.FORBIDDEN_ADMIN_ACCESS);
		}

		Post post = new Post().builder()
			.user(user)
			.title(dto.getTitle())
			.content(dto.getContent())
			.writer(user.getUserNickname())
			.view(0L)
			.postType(dto.getPostType())
			.postStatus(PostStatus.DISPLAYED)
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
	public CommonResponseDto<PaginationResponseDto<PostFindAllResponseDto>> findAllPost(PostType postType, String keyword, String writer, Pageable pageable) {

		// DB 에서 게시글 가져오기
		Page<PostFindAllResponseDto> posts = postRepository.findByTypeAndKeyword(postType, keyword, writer, pageable);

		PaginationResponseDto<PostFindAllResponseDto> findAllPostResult =
			PaginationResponseDto.<PostFindAllResponseDto>builder()
				.content(posts.getContent())
				.totalPages(posts.getTotalPages())
				.totalElements(posts.getTotalElements())
				.pageNumber(posts.getNumber())
				.pageSize(posts.getSize())
				.build();

		// CommonResponseDto에 Page를 직접 포함
		return CommonResponseDto.<PaginationResponseDto<PostFindAllResponseDto>>builder()
			.msg("게시글 목록 조회가 완료되었습니다.")
			.data(findAllPostResult)
			.build();
	}

	// 게시글 상세 조회
	@Override
	@Transactional
	public CommonResponseDto<PostFindResponseDto> findPost(Long postId) {

		Post post = postRepository.findByIdOrElseThrow(postId);

		ImageFile writerProfile = fileRepository.findByUserId(post.getUser().getId());

		post.increaseView(post.getView());

		PostFindResponseDto postFindResult= PostFindResponseDto.builder()
			.postId(post.getId())
			.title(post.getTitle())
			.writer(post.getWriter())
			.view(post.getView())
			.content(post.getContent())
			.postType(post.getPostType())
			.modifiedAt(post.getModifiedAt())
			.build();

		if (writerProfile != null) {
			postFindResult.inputWriterProfileUrl(writerProfile.getFilePath());
		}

		return CommonResponseDto.<PostFindResponseDto>builder()
			.msg("게시글 상세 조회가 완료되었습니다.")
			.data(postFindResult)
			.build();
	}

	// 게시글 수정
	@Override
	@Transactional
	public CommonResponseDto updatePost(Long userId, Long postId, PostUpdateRequestDto dto) {

		User user = userRepository.findByIdOrElseThrow(userId);
		Post post = postRepository.findByIdOrElseThrow(postId);

		if (!Objects.equals(user, post.getUser())) {
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}

		post.updatePost(dto.getTitle(), dto.getContent());

		return CommonResponseDto.builder()
			.msg("게시글 수정이 완료되었습니다.")
			.build();
	}

	// 게시글 삭제
	@Override
	@Transactional
	public CommonResponseDto deletePost(Long userId, Long postId) {

		User user = userRepository.findByIdOrElseThrow(userId);
		Post post = postRepository.findByIdOrElseThrow(postId);

		if (!Objects.equals(user, post.getUser())) {
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}

		post.deletePost();

		return CommonResponseDto.builder()
			.msg("게시글 삭제가 완료되었습니다.")
			.build();
	}
}

