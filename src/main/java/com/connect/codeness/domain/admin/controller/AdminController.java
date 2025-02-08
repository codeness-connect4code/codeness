package com.connect.codeness.domain.admin.controller;

import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;

import com.connect.codeness.domain.admin.dto.AdminMentorListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminSettlementListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminUpdateMentorRequestDto;
import com.connect.codeness.domain.admin.dto.MentorRequestDetailResponseDto;
import com.connect.codeness.domain.admin.service.AdminService;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.dto.PaginationResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	/**
	 * 멘토 정산 처리 API (멘토의 정산내역들을 정산해줌)
	 *
	 * @param mentorId 멘토 고유 식벽자
	 * @return 성공 메세지
	 */
	@PatchMapping("/mentors/{mentorId}/settlements")
	public ResponseEntity<CommonResponseDto<?>> updateSettlement(@PathVariable Long mentorId) {
		CommonResponseDto<?> commonResponseDto = adminService.updateSettlements(mentorId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 전체 멘토 리스트 조회 API
	 *
	 * @param pageNumber 페이지 넘버
	 * @param pageSize 페이지 크기
	 * @return 멘토 리스트(페이징)
	 */
	@GetMapping("/mentors")
	public ResponseEntity<CommonResponseDto<?>> getMentors(
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize) {
		CommonResponseDto<PaginationResponseDto<AdminMentorListResponseDto>> responseDto = adminService.getMentorList(
			pageNumber, pageSize);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 멘토 상세 조회 API
	 *
	 * @param mentorId 멘토 고유 식별자
	 * @return 멘토 상세 조회 내용
	 */
	@GetMapping("/mentors/{mentorId}")
	public ResponseEntity<CommonResponseDto<?>> getMentor(@PathVariable Long mentorId) {
		CommonResponseDto<?> commonResponseDto = adminService.getMentor(mentorId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 멘토 신청 리스트 조회 API(멘토를 신청한 내역들 조회)
	 *
	 * @return 맨토 신청 리스트
	 */
	@GetMapping("/mentors/mentor-requests")
	public ResponseEntity<CommonResponseDto<?>> getMentorRequests() {
		CommonResponseDto<List<MentorRequestResponseDto>> commonResponseDto = adminService.getMentorRequestList();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 멘토 신청 상세 조회 API
	 *
	 * @param mentorRequestId 멘토 신청 고유 식별자
	 * @return 멘토 신청 조회
	 */
	@GetMapping("/mentors/mentor-requests/{mentorRequestId}")
	public ResponseEntity<CommonResponseDto<?>> getMentorRequest(@PathVariable Long mentorRequestId) {
		CommonResponseDto<MentorRequestDetailResponseDto> commonResponseDto = adminService.getMentorRequest(
			mentorRequestId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 멘토 거절/수락 API
	 *
	 * @param mentorRequestId 멘토 신청 고유 식별자
	 * @param adminUpdateMentorRequestDto 거절/수락 dto
	 * @return 성공 메세지
	 */
	@PatchMapping("/mentors/mentor-requests/{mentorRequestId}")
	public ResponseEntity<CommonResponseDto<?>> updateMentor(@PathVariable Long mentorRequestId,
		@RequestBody AdminUpdateMentorRequestDto adminUpdateMentorRequestDto) {
		CommonResponseDto<?> commonResponseDto = adminService.updateMentor(mentorRequestId,
			adminUpdateMentorRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 멘토 정산 내역 조회 API
	 *
	 * @return 멘토 정산 내역 리스트
	 */
	@GetMapping("/mentors/settlements")
	public ResponseEntity<CommonResponseDto<?>> getSettlements() {
		CommonResponseDto<List<AdminSettlementListResponseDto>> commonResponseDto = adminService.getSettlementList();
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	@GetMapping("/mentors/settlements-detail/{mentorId}")
	public ResponseEntity<CommonResponseDto<?>> getSettlementDetail(@PathVariable Long mentorId) {
		CommonResponseDto<AdminSettlementListResponseDto> commonResponseDto = adminService.getSettlementDetail(mentorId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 멘토 정산 내역 상세 조회 API
	 *
	 * @param pageNumber 페이지 넘버
	 * @param pageSize 페이지 사이즈
	 * @param mentorId 멘토 고유 식별자
	 * @return 멘토 정산 내역 조회 리스트
	 */
	@GetMapping("/mentors/settlements/{mentorId}")
	public ResponseEntity<CommonResponseDto<?>> getSettlement(
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize, @PathVariable Long mentorId) {
		CommonResponseDto<?> commonResponseDto = adminService.getSettlement(mentorId, pageNumber,
			pageSize);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

}
