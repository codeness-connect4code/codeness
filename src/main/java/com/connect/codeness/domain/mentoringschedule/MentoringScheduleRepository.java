package com.connect.codeness.domain.mentoringschedule;

import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringScheduleRepository extends JpaRepository<MentoringSchedule, Long> {

	default MentoringSchedule findByIdOrElseThrow(Long id){
		return findById(id).orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTORING_SCHEDULE));
	}
}
