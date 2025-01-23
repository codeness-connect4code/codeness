package com.connect.codeness.domain.schedule.repository;

import com.connect.codeness.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<User, Long> {

}
