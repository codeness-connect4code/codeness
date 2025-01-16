package com.connect.codeness.domain.mentoringschedule;

import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
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

}
