package com.connect.codeness.domain.comment;

import com.connect.codeness.domain.comment.dto.CommentCreateRequestDto;
import com.connect.codeness.domain.post.Post;
import com.connect.codeness.domain.post.PostRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.CommunityStatus;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

	private final PostRepository postRepository;
	private CommentRepository commentRepository;

	public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
	}

	@Override
	public CommonResponseDto createComment(Long postId, CommentCreateRequestDto dto){

		Post post = postRepository.findByIdOrElseThrow(postId);

		Comment comment = new Comment().builder()
			.post(post)
			.content(dto.getContent())
			.status(CommunityStatus.DISPLAYED)
			.build();

		commentRepository.save(comment);

		return CommonResponseDto.builder()
			.msg("댓글 작성이 완료되었습니다.")
			.build();
	}

}

