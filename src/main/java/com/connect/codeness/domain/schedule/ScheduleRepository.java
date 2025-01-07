package com.connect.codeness.domain.schedule;

import com.connect.codeness.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<User, Long> {

}
