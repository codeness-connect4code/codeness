package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostRecommendResponseDto;
import com.connect.codeness.global.enums.FieldType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringPostRepository extends JpaRepository<MentoringPost, Long> {

	@Query(
		"SELECT new com.connect.codeness.domain.mentoringpost.dto.MentoringPostRecommendResponseDto(m, " +
			"COALESCE((SELECT CAST(AVG(r.starRating) AS DOUBLE) FROM Review r " +
			"WHERE r.paymentList.payment.mentoringSchedule.mentoringPost.id = m.id), 0.0)) " + // `CAST`로 `Double`로 변환
			"FROM MentoringPost AS m " +
			"WHERE (:field IS NULL OR m.field = :field) " +
			"AND (:region IS NULL OR m.region LIKE %:region%)"
	)
	Page<MentoringPostRecommendResponseDto> findByFilter(
		@Param("field") FieldType field,
		@Param("region") String region,
		Pageable pageable
	);


}
