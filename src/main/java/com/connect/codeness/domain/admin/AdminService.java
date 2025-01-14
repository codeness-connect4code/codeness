package com.connect.codeness.domain.admin;


import com.connect.codeness.domain.admin.dto.AdminUpdateMentorRequestDto;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.data.domain.Page;

public interface AdminService {
	CommonResponseDto<Page<UserResponseDto>> getMentors(int pageNumber, int pageSize);
	CommonResponseDto getMentor(Long mentorId);
	CommonResponseDto<Page<MentorRequestResponseDto>> getMentorRequests(int pageNumber, int pageSize);
	CommonResponseDto<MentorRequestResponseDto> getMentorRequest(Long mentoringRequestId);
	CommonResponseDto updateMentor(Long mentorId, AdminUpdateMentorRequestDto adminUpdateMentorRequestDto);
}

