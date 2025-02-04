package com.connect.codeness.domain.mentoringpost.repository;

import com.connect.codeness.domain.mentoringpost.entity.MentoringPost;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostRecommendResponseDto;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.MentoringPostStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringPostRepository extends JpaRepository<MentoringPost, Long>, MentoringPostRepositoryCustom {

	@Query("""
       SELECT new com.connect.codeness.domain.mentoringpost.dto.MentoringPostRecommendResponseDto(m,
       COALESCE((SELECT CAST(AVG(r.starRating) AS DOUBLE) FROM Review r
       WHERE r.mentoringPost.id = m.id), 0.0))
       FROM MentoringPost m
       WHERE (:field IS NULL AND :region IS NULL) OR
       (:field IS NOT NULL AND m.field = :field AND :region IS NOT NULL AND m.region LIKE %:region%) OR
       (:field IS NOT NULL AND m.field = :field AND :region IS NULL) OR
       (:field IS NULL AND :region IS NOT NULL AND m.region LIKE %:region%)
""")
	List<MentoringPostRecommendResponseDto> findByFilter(
		@Param("field") FieldType field,
		@Param("region") String region
	);
	Optional<MentoringPost> findByIdAndUserId(Long mentoringPostId, Long userId);

	default MentoringPost findByIdAndUserIdOrElseThrow(Long mentoringPostId, Long userId){
		return findByIdAndUserId(mentoringPostId, userId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTORING_POST));
	}

	Optional<MentoringPost> findById(Long mentoringPostId);

	default MentoringPost findByIdOrElseThrow(Long mentoringPostId){
		return findById(mentoringPostId).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTORING_POST));
	}

	@Query("SELECT m.user.id FROM MentoringPost m WHERE m.user.id = :userId")
	Long findUserIdByUserId(long userId);

	@Query("""
			  SELECT COUNT(mp) > 0
			  FROM MentoringPost mp
		      WHERE mp.user.id = :userId AND mp.mentoringPostStatus = 'DISPLAYED'
		""")
	boolean findMentoringPostStatusByUserId(long userId);

	/**
	 * TODO : 필요없으면 삭제
	 */
	Optional<MentoringPost> findByUserId(Long userId);

	Optional<MentoringPost> findByUserIdAndMentoringPostStatus(Long userId, MentoringPostStatus mentoringPostStatus);

}
