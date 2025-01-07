package com.connect.codeness.domain.mentorrequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringRequestRepository extends JpaRepository<MentoringRequest, Long> {

}
