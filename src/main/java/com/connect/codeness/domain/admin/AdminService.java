package com.connect.codeness.domain.admin;


import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.data.domain.Page;

public interface AdminService {
	CommonResponseDto<Page<UserResponseDto>> getMentors(int pageNumber, int pageSize);
	CommonResponseDto getMentor(Long mentorId);
}

