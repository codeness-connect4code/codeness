package com.connect.codeness.domain.comment;

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

	@Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.commentStatus <> 'DELETED'")
	Page<Comment> findCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

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
