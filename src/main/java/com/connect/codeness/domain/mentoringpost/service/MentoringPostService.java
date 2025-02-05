package com.connect.codeness.domain.mentoringpost.service;


import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostDetailResponseDto;
import com.connect.codeness.domain.mentoringpost.dto.MentoringPostSearchResponseDto;
import com.connect.codeness.domain.mentoringpost.dto.MyMentoringPostResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.dto.PaginationResponseDto;

public interface MentoringPostService {

	/**
	 * 멘토링 공고 생성 메서드
	 */
	CommonResponseDto<?> createMentoringPost(long userId, MentoringPostCreateRequestDto requestDto);

	/**
	 * 멘토링 공고 삭제 메서드
	 */
	CommonResponseDto<?> deleteMentoringPost(Long userId, Long mentoringPostId);

	/**
	 * 멘토링 공고 전체 조회 메서드
	 */
	CommonResponseDto<PaginationResponseDto<MentoringPostSearchResponseDto>> searchMentoringPosts(int pageNumber, int pageSize,
		String title, String field, String nickname);

	/**
	 * 멘토링 공고 상세 조회 메서드
	 */
	CommonResponseDto<MentoringPostDetailResponseDto> getMentoringPostDetail(Long mentoringPostId);

	/**
	 * 멘토가 생성한 멘토링 공고 조회 API
	 */
	CommonResponseDto<MyMentoringPostResponseDto> findMentoringPostByMentorId(Long userId);

	/**
	 * 멘티가 결제한 멘토링 공고 조회 API
	 */
	CommonResponseDto<PaginationResponseDto<MyMentoringPostResponseDto>> findMentoringPostByMenteeId(Long userId, int pageNumber,
		int pageSize);
}

