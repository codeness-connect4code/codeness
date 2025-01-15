package com.connect.codeness.domain.mentoringpost;


import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface MentoringPostService {

	/**
	 * 멘토링 공고 생성 메서드
	 */
	CommonResponseDto createMentoringPost(long userId, MentoringPostCreateRequestDto requestDto);

	/**
	 * 멘토링 공고 삭제 메서드
	 */
	CommonResponseDto deleteMentoringPost(Long userId, Long mentoringPostId);
}

