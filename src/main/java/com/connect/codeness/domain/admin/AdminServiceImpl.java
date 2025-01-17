package com.connect.codeness.domain.admin;

import com.connect.codeness.domain.admin.dto.AdminSettlementListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminSettlementResponseDto;
import com.connect.codeness.domain.admin.dto.AdminUpdateMentorRequestDto;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.mentorrequest.MentorRequest;
import com.connect.codeness.domain.mentorrequest.MentorRequestRepository;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.domain.paymenthistory.PaymentHistoryRepository;
import com.connect.codeness.domain.settlement.Settlement;
import com.connect.codeness.domain.settlement.SettlementRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.MentorRequestStatus;
import com.connect.codeness.global.enums.SettlementStatus;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.enums.UserStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;
	private final MentorRequestRepository mentorRequestRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final SettlementRepository settlementRepository;

	public AdminServiceImpl(UserRepository userRepository, MentorRequestRepository mentorRequestRepository,
		PaymentHistoryRepository paymentHistoryRepository,
		SettlementRepository settlementRepository) {
		this.userRepository = userRepository;
		this.mentorRequestRepository = mentorRequestRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
		this.settlementRepository = settlementRepository;
	}

	/* -----------------멘토 신청 관련 로직------------------ */

	@Override
	public CommonResponseDto<PaginationResponseDto<UserResponseDto>> getMentorList(int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

		Page<UserResponseDto> userResponseDtoPage = userRepository.findByRole(UserRole.MENTOR, pageable);

		PaginationResponseDto<UserResponseDto> pageUserResponseList =
			PaginationResponseDto.<UserResponseDto>builder()
				.content(userResponseDtoPage.getContent())
				.totalPages(userResponseDtoPage.getTotalPages())
				.totalElements(userResponseDtoPage.getTotalElements())
				.pageNumber(userResponseDtoPage.getNumber())
				.pageSize(userResponseDtoPage.getSize())
				.build();

		return CommonResponseDto.<PaginationResponseDto<UserResponseDto>>builder()
			.msg("전체 멘토 리스트 조회 되었습니다.")
			.data(pageUserResponseList)
			.build();
	}

	@Override
	public CommonResponseDto getMentor(Long mentorId) {
		User user = userRepository.findByIdOrElseThrow(mentorId);
		if (user.getRole() != UserRole.MENTOR) {
			throw new BusinessException(ExceptionType.NOT_MENTOR);
		}
		return CommonResponseDto.builder()
			.msg("멘토 상세 조회가 되었습니다.")
			.data(new UserResponseDto(user)).build();
	}

	@Override
	public CommonResponseDto<Page<MentorRequestResponseDto>> getMentorRequestList(int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
		Page<MentorRequestResponseDto> mentorRequestResponseDto
			= mentorRequestRepository.findByIsAccepted(MentorRequestStatus.WAITING, pageable);

		return CommonResponseDto.<Page<MentorRequestResponseDto>>builder()
			.msg("멘토 신청 리스트가 조회되었습니다.")
			.data(mentorRequestResponseDto)
			.build();
	}

	@Override
	public CommonResponseDto<MentorRequestResponseDto> getMentorRequest(Long mentoringRequestId) {
		MentorRequest mentorRequest = mentorRequestRepository.findByIdOrElseThrow(mentoringRequestId);

		return CommonResponseDto.<MentorRequestResponseDto>builder()
			.msg("멘토 신청 상세가 조회 되었습니다.")
			.data(new MentorRequestResponseDto(mentorRequest)).build();
	}

	@Override
	@Transactional
	public CommonResponseDto updateMentor(Long mentorRequestId,
		AdminUpdateMentorRequestDto dto) {
		MentorRequest mentorRequest = mentorRequestRepository.findByIdOrElseThrow(mentorRequestId);
		User user = userRepository.findByIdOrElseThrow(mentorRequest.getUser().getId());
		if (
			mentorRequest.getIsAccepted().equals(MentorRequestStatus.REJECTED)
				|| mentorRequest.getIsAccepted().equals(MentorRequestStatus.ACCEPTED)
		) {
			throw new BusinessException(ExceptionType.ALREADY_CLOSED_MENTOR_REQUEST);
		}
		if (dto.getIsAccepted().equals(MentorRequestStatus.ACCEPTED)) {
			user.updateRole(UserRole.MENTOR);
		}
		mentorRequest.updateStatus(dto.getIsAccepted());
		mentorRequestRepository.save(mentorRequest);
		userRepository.save(user);

		return CommonResponseDto.builder()
			.msg("멘토 신청 상태 변경이 완료되었습니다.").build();
	}

	/* -----------------멘토 정산 처리 관련 로직------------------ */

	@Override
	@Transactional
	public CommonResponseDto updateSettlements(Long mentorId) {
		List<Settlement> settlementList = settlementRepository.findAllByUserIdAndSettleStatus(mentorId,
			SettlementStatus.PROCESSING);

		if (settlementList.isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND);
		}
		for (Settlement s : settlementList){
			s.updateSettlementStatus(SettlementStatus.COMPLETE);
		}

		settlementRepository.saveAll(settlementList);
		return CommonResponseDto.builder()
			.msg("정산 처리가 완료되었습니다.").build();
	}

	@Override
	public CommonResponseDto<List<AdminSettlementListResponseDto>> getSettlementList() {
		List<AdminSettlementListResponseDto> adminSettlementGetResponseDto =
			settlementRepository.findBySettleStatusMentorGroupList(SettlementStatus.PROCESSING);

		return CommonResponseDto.<List<AdminSettlementListResponseDto>>builder()
			.msg("멘토 정산 내역이 조회되었습니다.")
			.data(adminSettlementGetResponseDto).build();
	}

	@Override
	public CommonResponseDto getSettlement(Long mentorId, int pageNumber, int pageSize) {
		List<AdminSettlementResponseDto> adminSettlementResponseDto =
			settlementRepository.findByUserIdAndSettleStatus(mentorId, SettlementStatus.PROCESSING);

		return CommonResponseDto.<List<AdminSettlementResponseDto>>builder()
			.msg("멘토의 정산 리스트가 조회되었습니다.")
			.data(adminSettlementResponseDto).build();
	}
}

