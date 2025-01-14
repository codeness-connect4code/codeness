package com.connect.codeness.domain.mentorrequest;

import com.connect.codeness.domain.file.FileService;
import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.enums.MentorRequestStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.io.IOException;
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
		MentorRequestCreateResponseDto dto, ImageFile imageFile){

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
}

