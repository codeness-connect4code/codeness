package com.connect.codeness.domain.mentorrequest.service;

import com.connect.codeness.domain.file.service.FileService;
import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestGetResponseDto;
import com.connect.codeness.domain.mentorrequest.entity.MentorRequest;
import com.connect.codeness.domain.mentorrequest.repository.MentorRequestRepository;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateRequestDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.MentorRequestStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MentorRequestServiceImpl implements MentorRequestService {

	private final MentorRequestRepository mentorRequestRepository;
	private final UserRepository userRepository;
	private final FileService fileService;

	public MentorRequestServiceImpl(MentorRequestRepository mentorRequestRepository,
		UserRepository userRepository, FileService fileService) {
		this.mentorRequestRepository = mentorRequestRepository;
		this.userRepository = userRepository;
		this.fileService = fileService;
	}

	//멘토 신청 api
	@Override
	@Transactional
	public CommonResponseDto createMentorRequest(
		Long userId,
		MentorRequestCreateRequestDto dto, ImageFile imageFile){

		User user = userRepository.findByIdOrElseThrow(userId);

		if (mentorRequestRepository.existsByUserIdAndIsAccepted(user.getId(), MentorRequestStatus.WAITING)
			|| mentorRequestRepository.existsByUserIdAndIsAccepted(user.getId(), MentorRequestStatus.ACCEPTED)) {
			throw new BusinessException(ExceptionType.MENTOR_REQUEST_LIMIT_EXCEEDED);
		}

		MentorRequest mentorRequest = MentorRequest.builder()
			.user(user)
			.company(dto.getCompany())
			.phoneNumber(dto.getPhoneNumber())
			.position(dto.getPosition())
			.career(dto.getCareer())
			.companyEmail(dto.getCompanyEmail())
			.isAccepted(MentorRequestStatus.WAITING)
			.field(dto.getField())
			.build();

		user.updateImageFiles(imageFile);

		userRepository.save(user);
		mentorRequestRepository.save(mentorRequest);

		return CommonResponseDto.builder().msg("멘토 신청 완료").build();
	}

	@Override
	@Transactional
	public CommonResponseDto deleteMentorRequest(Long tokenId, Long mentorRequestId) {
		MentorRequest mentorRequest = mentorRequestRepository.findById(mentorRequestId)
			.orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_MENTOR_REQUEST));

		if (mentorRequest.getIsAccepted() != MentorRequestStatus.REJECTED){
			throw new BusinessException(ExceptionType.UNAUTHORIZED_DELETE_REQUEST);
		}

		if (mentorRequest.getUser().getId() != tokenId){
			throw new BusinessException(ExceptionType.FORBIDDEN_PERMISSION);
		}

		mentorRequestRepository.delete(mentorRequest);
		return CommonResponseDto.builder().msg("멘토 신청 요청이 삭제되었습니다.").build();
	}

	@Override
	public CommonResponseDto getMentorRequest(Long tokenId) {
		List<MentorRequestGetResponseDto> mentorRequests =
			mentorRequestRepository.findAllByUserId(tokenId);

		return CommonResponseDto.builder().msg("멘토 신청이 조회되었습니다.").data(mentorRequests).build();
	}
}

