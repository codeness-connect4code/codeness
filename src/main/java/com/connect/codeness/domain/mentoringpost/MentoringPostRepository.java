package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostRecommendResponseDto;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringPostRepository extends JpaRepository<MentoringPost, Long>, MentoringPostRepositoryCustom {

	@Query(
		value = "SELECT new com.connect.codeness.domain.mentoringpost.dto.MentoringPostRecommendResponseDto(m, " +
			"COALESCE((SELECT CAST(AVG(r.starRating) AS DOUBLE) FROM Review r " +
			"WHERE r.mentoringPost.id = m.id), 0.0)) " +
			"FROM MentoringPost m " +
			"WHERE (:field IS NULL OR m.field = :field) " +
			"AND (:region IS NULL OR m.region LIKE %:region%)"
	)
	Page<MentoringPostRecommendResponseDto> findByFilter(
		@Param("field") FieldType field,
		@Param("region") String region,
		Pageable pageable
	);

	Optional<MentoringPost> findByIdAndUserId(Long mentoringPostId, Long userId);

	default MentoringPost findByIdAndUserIdOrElseThrow(Long mentoringPostId, Long userId){
		return findByIdAndUserId(mentoringPostId, userId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTORING_POST));
	}

	Optional<MentoringPost> findById(Long mentoringPostId);

	default MentoringPost findByIdOrElseThrow(Long mentoringPostId){
		return findById(mentoringPostId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTORING_POST));
	}
}
