package com.connect.codeness.domain.mentorrequest;


import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface MentorRequestService {
	CommonResponseDto createMentorRequest(Long userId, MentorRequestCreateResponseDto mentorRequestCreateResponseDto);
}

