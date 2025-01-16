package com.connect.codeness.domain.mentoringpost;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MentoringPostRepositoryCustom {

	Page<MentoringPost> findAllBySearchParameters(String title, String field, String nickname, Pageable pageable);

}
