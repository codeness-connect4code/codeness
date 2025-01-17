package com.connect.codeness.domain.mentorrequest;


import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface MentorRequestService {
	CommonResponseDto createMentorRequest(Long userId, MentorRequestCreateRequestDto mentorRequestCreateRequestDto, ImageFile imageFile);
	CommonResponseDto deleteMentorRequest(Long tokenId, Long mentorRequestId);
}

