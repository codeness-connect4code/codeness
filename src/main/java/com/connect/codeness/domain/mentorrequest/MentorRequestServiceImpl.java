package com.connect.codeness.domain.mentorrequest;

import com.connect.codeness.domain.file.FileService;
import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateRequestDto;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.MentorRequestStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
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

		if (mentorRequestRepository.existsByUserId(user.getId())){
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
}

