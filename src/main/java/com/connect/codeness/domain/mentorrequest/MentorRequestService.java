package com.connect.codeness.domain.mentorrequest;


import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface MentorRequestService {
	CommonResponseDto createMentorRequest(Long userId, MentorRequestCreateResponseDto mentorRequestCreateResponseDto, ImageFile imageFile);
}

