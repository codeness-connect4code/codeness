package com.connect.codeness.domain.comment;

import com.connect.codeness.domain.comment.dto.CommentCreateRequestDto;
import com.connect.codeness.domain.comment.dto.CommentFindAllResponseDto;
import com.connect.codeness.domain.file.FileRepository;
import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.post.Post;
import com.connect.codeness.domain.post.PostRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.global.enums.CommentStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

	private PostRepository postRepository;
	private CommentRepository commentRepository;
	private UserRepository userRepository;
	private FileRepository fileRepository;

	public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, FileRepository fileRepository) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.fileRepository = fileRepository;
	}

	@Override
	@Transactional
	public CommonResponseDto createComment(Long postId, Long userId, CommentCreateRequestDto dto){

		Post post = postRepository.findByIdOrElseThrow(postId);

		User user = userRepository.findByIdOrElseThrow(userId);

		ImageFile writerProfile = fileRepository.findByUserId(user.getId());

		Comment comment = new Comment().builder()
			.post(post)
			.user(user)
			.content(dto.getContent())
			.commentStatus(CommentStatus.DISPLAYED)
			.build();

		if (writerProfile!=null){
			comment.inputWriterProfileUrl(writerProfile.getFilePath());
		}

		commentRepository.save(comment);

		return CommonResponseDto.builder()
			.msg("댓글 작성이 완료되었습니다.")
			.build();
	}

	@Override
	public CommonResponseDto<PaginationResponseDto<CommentFindAllResponseDto>> findAllComment(Long postId, Pageable pageable){

		Page<CommentFindAllResponseDto> commentList = commentRepository.findCommentsByPostId(postId, pageable);

		PaginationResponseDto<CommentFindAllResponseDto> findAllCommentResult =
			PaginationResponseDto.<CommentFindAllResponseDto>builder()
				.content(commentList.getContent())
				.totalPages(commentList.getTotalPages())
				.totalElements(commentList.getTotalElements())
				.pageNumber(commentList.getNumber())
				.pageSize(commentList.getSize())
				.build();

		return CommonResponseDto.<PaginationResponseDto<CommentFindAllResponseDto>>builder()
			.msg("댓글 조회가 완료되었습니다.")
			.data(findAllCommentResult)
			.build();
	}

	@Override
	@Transactional
	public CommonResponseDto updateComment(Long commentId, Long userId, CommentCreateRequestDto dto){

		User user = userRepository.findByIdOrElseThrow(userId);

		Comment comment = commentRepository.findByIdOrElseThrow(commentId);

		if (!Objects.equals(user, comment.getUser())) {
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}

		comment.updateComment(dto.getContent());

		return CommonResponseDto.builder()
			.msg("댓글 수정이 완료되었습니다.")
			.build();
	}

	@Override
	@Transactional
	public CommonResponseDto deleteComment(Long commentId, Long userId){

		User user = userRepository.findByIdOrElseThrow(userId);

		Comment comment = commentRepository.findByIdOrElseThrow(commentId);

		if (!Objects.equals(user, comment.getUser())) {
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}

		comment.deleteComment();

		return CommonResponseDto.builder()
			.msg("댓글 삭제가 완료되었습니다.")
			.build();
	}
}

