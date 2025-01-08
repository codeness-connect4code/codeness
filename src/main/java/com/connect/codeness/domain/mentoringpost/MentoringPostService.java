package com.connect.codeness.domain.mentoringpost;


import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface MentoringPostService {

	CommonResponseDto createMentoringPost(long userId, MentoringPostCreateRequestDto requestDto);
}

