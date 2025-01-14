package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostCreateRequestDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FieldType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MentoringPostServiceImpl implements MentoringPostService {

	private final MentoringPostRepository mentoringPostRepository;
	private final UserRepository userRepository;

	public MentoringPostServiceImpl(MentoringPostRepository mentoringPostRepository,
		UserRepository userRepository) {
		this.mentoringPostRepository = mentoringPostRepository;
		this.userRepository = userRepository;
	}

	/**
	 * 멘토링 공고 생성 서비스 메서드
	 */
	@Override
	@Transactional
	public CommonResponseDto createMentoringPost(long userId, MentoringPostCreateRequestDto requestDto) {
		//유저 조회
		User user = userRepository.findByIdOrElseThrow(userId);

		// FieldType 변환
		FieldType fieldType = FieldType.fromString(requestDto.getField().name());

		MentoringPost mentoringPost = MentoringPost.builder()
						.user(user)
						.title(requestDto.getTitle())
						.company(requestDto.getCompany())
						.field(fieldType)
						.career(requestDto.getCareer())
						.region(requestDto.getRegion())
						.price(requestDto.getPrice())
						.startDate(requestDto.getStartDate())
						.endDate(requestDto.getEndDate())
						.startTime(requestDto.getStartTime())
						.endTime(requestDto.getEndTime())
						.description(requestDto.getDescription())
						.build();

		//db 저장
		mentoringPostRepository.save(mentoringPost);

		return CommonResponseDto.builder().msg("멘토링 공고 생성이 완료되었습니다.").build();
	}
}
