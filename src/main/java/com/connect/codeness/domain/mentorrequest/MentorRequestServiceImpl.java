package com.connect.codeness.domain.mentorrequest;

import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateResponseDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.MentorRequestAccepted;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MentorRequestServiceImpl implements MentorRequestService {

	private final MentorRequestRepository mentorRequestRepository;
	private final UserRepository userRepository;

	public MentorRequestServiceImpl(MentorRequestRepository mentorRequestRepository,
		UserRepository userRepository) {
		this.mentorRequestRepository = mentorRequestRepository;
		this.userRepository = userRepository;
	}

	//멘토 신청 api
	@Override
	@Transactional
	public CommonResponseDto createMentorRequest(
		Long userId,
		MentorRequestCreateResponseDto dto) {

		User user = userRepository.findByIdOrElseThrow(userId);

		if (mentorRequestRepository.existsByUserId(user.getId())){
			throw new BusinessException(ExceptionType.MENTOR_REQUEST_LIMIT_EXCEEDED);
		}

		MentorRequest mentorRequest = MentorRequest.builder()
			.user(user).company(dto.getCompany()).phoneNumber(dto.getPhoneNumber())
			.position(dto.getPosition()).career(dto.getCareer()).companyEmail(dto.getCompanyEmail())
			.isAccepted(MentorRequestAccepted.WAITING).field(dto.getField()).build();

			mentorRequestRepository.save(mentorRequest);

			return CommonResponseDto.builder().msg("멘토 신청 완료").build();
	}
}

