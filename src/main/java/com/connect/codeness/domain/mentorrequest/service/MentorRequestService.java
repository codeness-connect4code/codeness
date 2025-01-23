package com.connect.codeness.domain.mentorrequest.service;


import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface MentorRequestService {
	CommonResponseDto createMentorRequest(Long userId, MentorRequestCreateRequestDto mentorRequestCreateRequestDto, ImageFile imageFile);
	CommonResponseDto deleteMentorRequest(Long tokenId, Long mentorRequestId);
	CommonResponseDto getMentorRequest(Long tokenId);
}

