package com.connect.codeness.domain.mentoringpost.repository;


import com.connect.codeness.domain.mentoringpost.dto.MentoringPostSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MentoringPostRepositoryCustom {

	Page<MentoringPostSearchResponseDto> findAllBySearchParameters(String title, String field, String nickname, Pageable pageable);

}
