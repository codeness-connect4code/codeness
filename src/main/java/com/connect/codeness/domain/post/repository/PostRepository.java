package com.connect.codeness.domain.post.repository;

import com.connect.codeness.domain.post.dto.PostFindAllResponseDto;
import com.connect.codeness.domain.post.entity.Post;
import com.connect.codeness.global.enums.PostStatus;
import com.connect.codeness.global.enums.PostType;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

	@Query("SELECT new com.connect.codeness.domain.post.dto.PostFindAllResponseDto(p.id, p.title, p.writer, p.view, p.createdAt) " +
		"FROM Post p " +
		"WHERE (:type IS NULL OR p.postType = :type) " +
		"AND (:keyword IS NULL OR p.title LIKE CONCAT(:keyword, '%')) " +
		"AND (:writer IS NULL OR p.writer LIKE CONCAT(:writer, '%')) " +
		"AND p.postStatus = 'DISPLAYED'")
	Page<PostFindAllResponseDto> findByTypeAndKeyword(
		@Param("type") PostType type,
		@Param("keyword") String keyword,
		@Param("writer") String writer,
		Pageable pageable
	);

	List<PostFindAllResponseDto> findTop10ByCreatedAtAfterOrderByViewDesc(LocalDateTime createdAt);

	@Modifying
	@Query("UPDATE Post p SET p.view = p.view + 1 WHERE p.id = :postId")
	void increaseViewCount(@Param("postId") Long postId);

}
