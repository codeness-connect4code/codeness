package com.connect.codeness.domain.comment;

import com.connect.codeness.domain.comment.dto.CommentCreateRequestDto;
import com.connect.codeness.domain.post.Post;
import com.connect.codeness.domain.post.PostRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.CommunityStatus;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

	private PostRepository postRepository;
	private CommentRepository commentRepository;
	private UserRepository userRepository;

	public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}

	@Override
	public CommonResponseDto createComment(Long postId, Long userId, CommentCreateRequestDto dto){

		Post post = postRepository.findByIdOrElseThrow(postId);

		User user = userRepository.findByIdOrElseThrow(userId);

		Comment comment = new Comment().builder()
			.post(post)
			.user(user)
			.content(dto.getContent())
			.status(CommunityStatus.DISPLAYED)
			.build();

		commentRepository.save(comment);

		return CommonResponseDto.builder()
			.msg("댓글 작성이 완료되었습니다.")
			.build();
	}

}

