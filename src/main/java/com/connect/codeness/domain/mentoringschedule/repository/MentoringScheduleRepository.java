package com.connect.codeness.domain.mentoringschedule.repository;

import com.connect.codeness.domain.mentoringschedule.entity.MentoringSchedule;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.enums.BookedStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

}
