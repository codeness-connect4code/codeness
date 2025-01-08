package com.connect.codeness.domain.mentorrequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorRequestRepository extends JpaRepository<MentorRequest, Long> {
	boolean existsByUserId(Long userId);
}
