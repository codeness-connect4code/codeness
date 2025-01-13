package com.connect.codeness.domain.comment;


import com.connect.codeness.domain.comment.dto.CommentCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface CommentService {

	CommonResponseDto createComment(Long postId, Long userId, CommentCreateRequestDto commentCreateRequestDto);

}

