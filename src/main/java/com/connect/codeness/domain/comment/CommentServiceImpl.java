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
import com.connect.codeness.global.enums.CommentStatus;
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
	public CommonResponseDto<Page<CommentFindAllResponseDto>> findAllComment(Long postId, Pageable pageable){

		Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

		Page<CommentFindAllResponseDto> commentList = comments.map(comment -> CommentFindAllResponseDto.builder()
			.postId(postId)
			.commentId(comment.getId())
			.content(comment.getContent())
			.writer(comment.getUser().getUserNickname())
			.writerProfileUrl(comment.getWriterProfileUrl())
			.build());

		return CommonResponseDto.<Page<CommentFindAllResponseDto>>builder()
			.msg("댓글 조회가 완료되었습니다.")
			.data(commentList)
			.build();
	}

}

