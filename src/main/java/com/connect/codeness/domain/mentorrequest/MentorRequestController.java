package com.connect.codeness.domain.mentorrequest;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;

import com.connect.codeness.domain.file.FileRepository;
import com.connect.codeness.domain.file.FileService;
import com.connect.codeness.domain.file.ImageFile;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateRequestDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class MentorRequestController {

	private final MentorRequestService mentorRequestService;
	private final JwtUtil jwtUtil;
	private final FileRepository fileRepository;
	private final FileService fileService;

	public MentorRequestController(MentorRequestService mentorRequestService, JwtUtil jwtUtil, FileRepository fileRepository, FileService fileService) {
		this.mentorRequestService = mentorRequestService;
		this.jwtUtil = jwtUtil;
		this.fileRepository = fileRepository;
		this.fileService = fileService;
	}

	//todo : 파일 service 구현시 파일 추가로 수정 (사원증 이미지 첨부), token 구현시 사용자 정보 받아오기 수정(현재는 임의의 userid 1 전송)
	@PostMapping("/mentors")
	public ResponseEntity<CommonResponseDto> createMentorRequest(
		@RequestHeader(AUTHORIZATION) String authorizationHeader,
		@Valid @ModelAttribute MentorRequestCreateRequestDto mentorRequestCreateRequestDto
	) throws IOException {
		String token = authorizationHeader.substring("Bearer ".length());
		Long tokenId = jwtUtil.extractUserId(token);

		if (
			fileRepository.findByUserIdAndFileCategory(tokenId, FileCategory.EMPLOYEE_CARD).isPresent()
		){
			fileService.deleteFile(tokenId,FileCategory.EMPLOYEE_CARD);
		}

		CommonResponseDto fileDto =
			fileService.createFile(
				mentorRequestCreateRequestDto.getMultipartFile(),tokenId,FileCategory.EMPLOYEE_CARD
			);
		ImageFile imageFile = fileRepository.findByUserIdAndFileCategoryOrElseThrow(tokenId,FileCategory.EMPLOYEE_CARD);
		CommonResponseDto response = mentorRequestService.createMentorRequest(
			tokenId, mentorRequestCreateRequestDto, imageFile);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}