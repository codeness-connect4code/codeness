package com.connect.codeness.domain.admin.service;


import com.connect.codeness.domain.admin.dto.AdminMentorListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminSettlementListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminUpdateMentorRequestDto;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;

public interface AdminService {
	CommonResponseDto<PaginationResponseDto<AdminMentorListResponseDto>> getMentorList(int pageNumber, int pageSize);
	CommonResponseDto getMentor(Long mentorId);
	CommonResponseDto<List<MentorRequestResponseDto>> getMentorRequestList();
	CommonResponseDto<MentorRequestResponseDto> getMentorRequest(Long mentoringRequestId);
	CommonResponseDto updateMentor(Long mentorId, AdminUpdateMentorRequestDto adminUpdateMentorRequestDto);
	CommonResponseDto updateSettlements(Long mentorId);
	CommonResponseDto<List<AdminSettlementListResponseDto>> getSettlementList();
	CommonResponseDto<AdminSettlementListResponseDto> getSettlementDetail(Long mentorId);
	CommonResponseDto getSettlement(Long mentorId, int pageNumber, int pageSize);
}

