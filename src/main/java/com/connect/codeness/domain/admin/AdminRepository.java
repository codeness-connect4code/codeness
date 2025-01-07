package com.connect.codeness.domain.admin;

import com.connect.codeness.domain.mentoringpost.MentoringPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<MentoringPost, Long> {

}
