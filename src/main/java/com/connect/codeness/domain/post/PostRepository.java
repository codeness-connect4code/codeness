package com.connect.codeness.domain.post;

import com.connect.codeness.global.enums.PostStatus;
import com.connect.codeness.global.enums.PostType;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	default Post findByIdOrElseThrow(Long id) {
		Post post = findById(id).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_POST)
		);

		if (post.getPostStatus() != PostStatus.DISPLAYED) {
			throw new BusinessException(ExceptionType.NOT_FOUND_POST);
		}
		return post;
	}

	@Query("SELECT p FROM Post p WHERE (:type IS NULL OR p.postType = :type) " +
		"AND (:keyword IS NULL OR p.title LIKE %:keyword%) " +
		"AND (:writer IS NULL OR p.writer = :writer) " +
		"AND p.postStatus != 'DELETED'")
	Page<Post> findByTypeAndKeyword(@Param("type") PostType type,
		@Param("keyword") String keyword,
		@Param("writer") String writer,
		Pageable pageable);
}
