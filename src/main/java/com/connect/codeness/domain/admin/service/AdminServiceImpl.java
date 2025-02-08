package com.connect.codeness.domain.admin.service;

import com.connect.codeness.domain.admin.dto.AdminMentorListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminSettlementListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminSettlementResponseDto;
import com.connect.codeness.domain.admin.dto.AdminUpdateMentorRequestDto;
import com.connect.codeness.domain.admin.dto.MentorRequestDetailResponseDto;
import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.file.repository.FileRepository;
import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.mentorrequest.entity.MentorRequest;
import com.connect.codeness.domain.mentorrequest.repository.MentorRequestRepository;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.domain.settlement.entity.Settlement;
import com.connect.codeness.domain.settlement.repository.SettlementRepository;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.enums.MentorRequestStatus;
import com.connect.codeness.global.enums.SettlementStatus;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import java.util.Optional;
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
	private final SettlementRepository settlementRepository;
	private final FileRepository fileRepository;

	public AdminServiceImpl(UserRepository userRepository, MentorRequestRepository mentorRequestRepository,
		SettlementRepository settlementRepository, FileRepository fileRepository) {
		this.userRepository = userRepository;
		this.mentorRequestRepository = mentorRequestRepository;
		this.settlementRepository = settlementRepository;
		this.fileRepository = fileRepository;
	}

	/* -----------------멘토 신청 관련 로직------------------ */

	/**
	 * 전체 멘토 리스트 페이지 조회
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@Override
	public CommonResponseDto<PaginationResponseDto<AdminMentorListResponseDto>> getMentorList(int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

		//멘토인 사용자만 조회
		Page<AdminMentorListResponseDto> userResponseDtoPage = userRepository.findByRole(UserRole.MENTOR, pageable);

		PaginationResponseDto<AdminMentorListResponseDto> pageUserResponseList =
			PaginationResponseDto.<AdminMentorListResponseDto>builder()
				.content(userResponseDtoPage.getContent())
				.totalPages(userResponseDtoPage.getTotalPages())
				.totalElements(userResponseDtoPage.getTotalElements())
				.pageNumber(userResponseDtoPage.getNumber())
				.pageSize(userResponseDtoPage.getSize())
				.build();

		return CommonResponseDto.<PaginationResponseDto<AdminMentorListResponseDto>>builder()
			.msg("전체 멘토 리스트 조회 되었습니다.")
			.data(pageUserResponseList)
			.build();
	}

	/**
	 * 멘토 상세(단건) 정보 조회
	 * @param mentorId
	 * @return
	 */
	@Override
	public CommonResponseDto getMentor(Long mentorId) {
		User user = userRepository.findByIdOrElseThrow(mentorId);
		//멘토일시만 조회
		if (user.getRole() != UserRole.MENTOR) {
			throw new BusinessException(ExceptionType.NOT_MENTOR);
		}

		Optional<ImageFile> file = fileRepository.findByUserIdAndFileCategory(mentorId, FileCategory.PROFILE);
		String fileUrl = "";

		if (file.isEmpty()){
			fileUrl = "https://codeness.s3.ap-northeast-1.amazonaws.com/Profile/1-Profile.jpg";
		}else {
			fileUrl = file.get().getFilePath();
		}

		return CommonResponseDto.builder()
			.msg("멘토 상세 조회가 되었습니다.")
			.data(new UserResponseDto(user,fileUrl)).build();
	}

	/**
	 * 멘토 신청 리스트 조회
	 * @return
	 */
	@Override
	public CommonResponseDto<List<MentorRequestResponseDto>> getMentorRequestList() {
		//멘토 신청이 대기중인 데이터만 조회
		List<MentorRequestResponseDto> mentorRequestResponseDto
			= mentorRequestRepository.findByIsAccepted(MentorRequestStatus.WAITING);

		return CommonResponseDto.<List<MentorRequestResponseDto>>builder()
			.msg("멘토 신청 리스트가 조회되었습니다.")
			.data(mentorRequestResponseDto)
			.build();
	}

	/**
	 * 멘토 신청 상세(단건) 조회
	 * @param mentoringRequestId
	 * @return
	 */
	@Override
	public CommonResponseDto<MentorRequestDetailResponseDto> getMentorRequest(Long mentoringRequestId) {
		MentorRequest mentorRequest = mentorRequestRepository.findByIdOrElseThrow(mentoringRequestId);
		Optional<ImageFile> file = fileRepository.findByUserIdAndFileCategory(mentorRequest.getUser().getId(),FileCategory.EMPLOYEE_CARD);
		String fileUrl = "";
		if (file.isEmpty()){
			fileUrl = "https://codeness.s3.ap-northeast-1.amazonaws.com/Profile/1-Profile.jpg";
		}else {
			fileUrl = file.get().getFilePath();
		}
		return CommonResponseDto.<MentorRequestDetailResponseDto>builder()
			.msg("멘토 신청 상세가 조회 되었습니다.")
			.data(new MentorRequestDetailResponseDto(mentorRequest,fileUrl)).build();
	}

	/**
	 * 멘토 신청 수락/거절
	 * @param mentorRequestId
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional
	public CommonResponseDto updateMentor(Long mentorRequestId,
		AdminUpdateMentorRequestDto dto) {
		MentorRequest mentorRequest = mentorRequestRepository.findByIdOrElseThrow(mentorRequestId);
		User user = userRepository.findByIdOrElseThrow(mentorRequest.getUser().getId());

		//멘토 신청 상태가 대기중일 때만 변경 가능
		if (
			mentorRequest.getIsAccepted().equals(MentorRequestStatus.REJECTED)
				|| mentorRequest.getIsAccepted().equals(MentorRequestStatus.ACCEPTED)
		) {
			throw new BusinessException(ExceptionType.ALREADY_CLOSED_MENTOR_REQUEST);
		}

		//멘토 신청 수락시 신청한 유저의 Role 변경
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

	/**
	 * 멘토 정산 요청 수락
	 * @param mentorId
	 * @return
	 */
	@Override
	@Transactional
	public CommonResponseDto updateSettlements(Long mentorId) {
		List<Settlement> settlementList = settlementRepository.findAllByUserIdAndSettleStatus(mentorId,
			SettlementStatus.PROCESSING);

		if (settlementList.isEmpty()) {
			throw new BusinessException(ExceptionType.NOT_FOUND);
		}
		//todo : 멘토가 정산 요청한 리스트의 정산건들을 전부 COMPLETE로 변경(후에 최적화)
		for (Settlement s : settlementList){
			s.updateSettlementStatus(SettlementStatus.COMPLETE);
		}

		settlementRepository.saveAll(settlementList);
		return CommonResponseDto.builder()
			.msg("정산 처리가 완료되었습니다.").build();
	}

	/**
	 * 멘토들의 정산 신청 리스트 조회
	 * @return
	 */
	@Override
	public CommonResponseDto<List<AdminSettlementListResponseDto>> getSettlementList() {
		//대기중(processing)인 정산 요청만 조회
		List<AdminSettlementListResponseDto> adminSettlementGetResponseDto =
			settlementRepository.findBySettleStatusMentorGroupList(SettlementStatus.PROCESSING);

		return CommonResponseDto.<List<AdminSettlementListResponseDto>>builder()
			.msg("멘토 정산 내역이 조회되었습니다.")
			.data(adminSettlementGetResponseDto).build();
	}

	@Override
	public CommonResponseDto<AdminSettlementListResponseDto> getSettlementDetail(
		Long mentorId) {
		//대기중(processing)인 정산 요청만 조회
		AdminSettlementListResponseDto adminSettlementGetResponseDto =
			settlementRepository.findBySettleStatusMentorDetail(mentorId,SettlementStatus.PROCESSING);

		return CommonResponseDto.<AdminSettlementListResponseDto>builder()
			.msg("멘토 정산 내역이 조회되었습니다.")
			.data(adminSettlementGetResponseDto).build();
	}

	/**
	 * 한 명의 멘토의 정산 리스트 조회
	 * @param mentorId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@Override
	public CommonResponseDto getSettlement(Long mentorId, int pageNumber, int pageSize) {
		//processing(대기중) 인 정산 요청만 조회
		List<AdminSettlementResponseDto> adminSettlementResponseDto =
			settlementRepository.findByUserIdAndSettleStatus(mentorId, SettlementStatus.PROCESSING);

		return CommonResponseDto.<List<AdminSettlementResponseDto>>builder()
			.msg("멘토의 정산 리스트가 조회되었습니다.")
			.data(adminSettlementResponseDto).build();
	}
}

