package com.connect.codeness.domain.mentoringschedule.repository;

import com.connect.codeness.domain.mentoringpost.dto.MyMentoringPostResponseDto;
import com.connect.codeness.domain.mentoringschedule.entity.MentoringSchedule;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.enums.BookedStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringScheduleRepository extends JpaRepository<MentoringSchedule, Long> {

	default MentoringSchedule findByIdOrElseThrow(Long id){
		return findById(id).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTORING_SCHEDULE));
	}

	@Query("SELECT m.mentoringPost.user FROM MentoringSchedule m WHERE m.id = :mentoringScheduleId")
	User findMentorById(Long mentoringScheduleId);

	List<MentoringSchedule> findByMentoringPostId(Long mentoringPostId);

	@Query("""
	SELECT ms
	FROM MentoringSchedule ms
	WHERE ms.mentoringPost.id = :mentoringPostId
	AND ms.bookedStatus = :bookedStatus
	AND	(ms.mentoringDate > :currentDate 
		OR (ms.mentoringDate = :currentDate AND ms.mentoringTime >= :currentTime))
	""")
	List<MentoringSchedule> findValidMentoringSchedules(Long mentoringPostId, BookedStatus bookedStatus, LocalDate currentDate, LocalTime currentTime);

	@Query("""
		SELECT DISTINCT new com.connect.codeness.domain.mentoringpost.dto.MyMentoringPostResponseDto(
			mp.id,
			u.userNickname,
			mp.field,
			mp.title,
			mp.career,
			CAST(COALESCE(AVG(r.starRating), 0.0) AS double)
			)
		FROM MentoringSchedule ms
		JOIN ms.mentoringPost mp
		JOIN mp.user u
		LEFT JOIN Review r ON r.mentoringPost.id = mp.id 
		WHERE ms.id IN :mentoringScheduleIds
		GROUP BY mp.id, u.userNickname, mp.field, mp.title, mp.career
		ORDER BY mp.createdAt DESC
	""")
	Page<MyMentoringPostResponseDto> findMentoringPostById(List<Long> mentoringScheduleIds,  Pageable pageable);
}
