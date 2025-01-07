package com.connect.codeness.domain.mentoringpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringPostRepository extends JpaRepository<MentoringPost, Long> {

}
