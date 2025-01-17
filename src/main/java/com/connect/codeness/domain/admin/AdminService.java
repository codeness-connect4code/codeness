package com.connect.codeness.domain.admin;


import com.connect.codeness.domain.admin.dto.AdminMentorListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminSettlementListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminUpdateMentorRequestDto;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;
import org.springframework.data.domain.Page;

public interface AdminService {
	CommonResponseDto<PaginationResponseDto<AdminMentorListResponseDto>> getMentorList(int pageNumber, int pageSize);
	CommonResponseDto getMentor(Long mentorId);
	CommonResponseDto<List<MentorRequestResponseDto>> getMentorRequestList();
	CommonResponseDto<MentorRequestResponseDto> getMentorRequest(Long mentoringRequestId);
	CommonResponseDto updateMentor(Long mentorId, AdminUpdateMentorRequestDto adminUpdateMentorRequestDto);
	CommonResponseDto updateSettlements(Long mentorId);
	CommonResponseDto<List<AdminSettlementListResponseDto>> getSettlementList();
	CommonResponseDto getSettlement(Long mentorId, int pageNumber, int pageSize);
}

