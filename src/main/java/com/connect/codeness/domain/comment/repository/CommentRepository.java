package com.connect.codeness.domain.comment.repository;

import com.connect.codeness.domain.comment.dto.CommentFindAllResponseDto;
import com.connect.codeness.domain.comment.entity.Comment;
import com.connect.codeness.global.enums.CommentStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("""
	SELECT new com.connect.codeness.domain.comment.dto.CommentFindAllResponseDto(
		c.post.id,c.user.id, c.id,c.content,c.user.userNickname,i.filePath,c.createdAt)
		FROM Comment c
		LEFT JOIN c.user.imageFiles i
		WHERE c.post.id = :postId
		AND c.commentStatus <> 'DELETED'
		AND (i.fileCategory = 'PROFILE' OR i IS NULL)
		""")
	Page<CommentFindAllResponseDto> findCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

	default Comment findByIdOrElseThrow(Long id) {
		Comment comment = findById(id).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_COMMENT)
		);

		if (comment.getCommentStatus() != CommentStatus.DISPLAYED) {
			throw new BusinessException(ExceptionType.NOT_FOUND_COMMENT);
		}
		return comment;
	}
}
