package com.connect.codeness.domain.comment;


import com.connect.codeness.domain.comment.dto.CommentCreateRequestDto;
import com.connect.codeness.domain.comment.dto.CommentFindAllResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

	CommonResponseDto createComment(Long postId, Long userId, CommentCreateRequestDto commentCreateRequestDto);

	CommonResponseDto<Page<CommentFindAllResponseDto>> findAllComment(Long postId, Pageable pageable);
}

