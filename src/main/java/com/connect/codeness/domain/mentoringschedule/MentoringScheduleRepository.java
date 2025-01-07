package com.connect.codeness.domain.mentoringschedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringScheduleRepository extends JpaRepository<MentoringSchedule, Long> {

}
