package com.connect.codeness.domain.admin;

import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;

	public AdminServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
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
}

