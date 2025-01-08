package com.connect.codeness.domain.post;

import com.connect.codeness.global.enums.CommunityStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	default Post findByIdOrElseThrow(Long id) {
		Post post = findById(id).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_POST)
		);

		if (post.getStatus() == CommunityStatus.DELETED){
			throw new BusinessException(ExceptionType.NOT_FOUND_POST);
		}
		return post;
	}
}
