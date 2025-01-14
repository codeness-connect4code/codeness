package com.connect.codeness.domain.admin;

import com.connect.codeness.domain.admin.dto.AdminSettlementGetResponseDto;
import com.connect.codeness.domain.admin.dto.AdminUpdateMentorRequestDto;
import com.connect.codeness.domain.mentorrequest.MentorRequest;
import com.connect.codeness.domain.mentorrequest.MentorRequestRepository;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.domain.paymenthistory.PaymentHistory;
import com.connect.codeness.domain.paymenthistory.PaymentHistoryRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.MentorRequestStatus;
import com.connect.codeness.global.enums.SettleStatus;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Set;
import org.apache.coyote.BadRequestException;
import org.aspectj.weaver.Lint;
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

	public AdminServiceImpl(UserRepository userRepository, MentorRequestRepository mentorRequestRepository,
		PaymentHistoryRepository paymentHistoryRepository) {
		this.userRepository = userRepository;
		this.mentorRequestRepository = mentorRequestRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
	}

	@Override
	public CommonResponseDto<Page<UserResponseDto>> getMentors(int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
		Page<UserResponseDto> userResponseDto = userRepository.findByRole(UserRole.MENTOR, pageable);
		return CommonResponseDto.<Page<UserResponseDto>>builder()
			.msg("전체 멘토 리스트 조회 되었습니다.")
			.data(userResponseDto)
			.build();
	}

	@Override
	public CommonResponseDto getMentor(Long mentorId) {
		User user = userRepository.findByIdOrElseThrow(mentorId);
		if (user.getRole() != UserRole.MENTOR) {
			throw new BusinessException(ExceptionType.BAD_REQUEST);
		}
		return CommonResponseDto.builder()
			.msg("멘토 상세 조회가 되었습니다.")
			.data(new UserResponseDto(user)).build();
	}

	@Override
	public CommonResponseDto<Page<MentorRequestResponseDto>> getMentorRequests(int pageNumber, int pageSize) {
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
		MentorRequest mentorRequest = mentorRequestRepository.findById(mentoringRequestId)
			.orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND));

		return CommonResponseDto.<MentorRequestResponseDto>builder()
			.msg("멘토 신청 상세가 조회 되었습니다.")
			.data(new MentorRequestResponseDto(mentorRequest)).build();
	}

	@Override
	@Transactional
	public CommonResponseDto updateMentor(Long mentorRequestId,
		AdminUpdateMentorRequestDto dto) {
		MentorRequest mentorRequest = mentorRequestRepository.findById(mentorRequestId).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND)
		);
		if (
			mentorRequest.getIsAccepted().equals(MentorRequestStatus.REJECTED)|| mentorRequest.getIsAccepted().equals(MentorRequestStatus.ACCEPTED)
		) {
			throw new BusinessException(ExceptionType.BAD_REQUEST);
		}
		mentorRequest.updateStatus(dto.getIsAccepted());
		mentorRequestRepository.save(mentorRequest);
		return CommonResponseDto.builder()
			.msg("멘토 신청 상태 변경이 완료되었습니다.").build();
	}

	@Override
	@Transactional
	public CommonResponseDto updateSettlement(Long mentorId) {
		PaymentHistory paymentHistory = paymentHistoryRepository.findByUserId(mentorId).orElseThrow(
			()-> new BusinessException(ExceptionType.NOT_FOUND)
		);

		if (paymentHistory.getSettleStatus().equals(SettleStatus.UNPROCESSED) || paymentHistory.getPaymentStatus().equals(SettleStatus.COMPLETE)){
			throw new BusinessException(ExceptionType.BAD_REQUEST);
		}

		paymentHistory.updateSettleStatus(SettleStatus.COMPLETE);
		paymentHistoryRepository.save(paymentHistory);

		return CommonResponseDto.builder()
			.msg("정산 처리가 완료되었습니다.").build();
	}

	@Override
	public CommonResponseDto<Page<AdminSettlementGetResponseDto>> getSettlements(
		int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
		Page<AdminSettlementGetResponseDto> adminSettlementGetResponseDto =
			paymentHistoryRepository.findMentorGroupList(pageable);

		return CommonResponseDto.<Page<AdminSettlementGetResponseDto>>builder()
			.msg("멘토 정산 내역이 조회되었습니다.")
			.data(adminSettlementGetResponseDto).build();
	}
}

