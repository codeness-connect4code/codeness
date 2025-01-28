package com.connect.codeness.domain.mentorrequest.controller;

import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN;

import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.file.repository.FileRepository;
import com.connect.codeness.domain.file.service.FileService;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestCreateRequestDto;
import com.connect.codeness.domain.mentorrequest.service.MentorRequestService;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class MentorRequestController {

	private final MentorRequestService mentorRequestService;
	private final JwtProvider jwtProvider;
	private final FileRepository fileRepository;
	private final FileService fileService;

	public MentorRequestController(MentorRequestService mentorRequestService,
		JwtProvider jwtProvider, FileRepository fileRepository, FileService fileService) {
		this.mentorRequestService = mentorRequestService;
		this.jwtProvider = jwtProvider;
		this.fileRepository = fileRepository;
		this.fileService = fileService;
	}

	/**
	 * 멘토 신청 API
	 *
	 * @param request
	 * @param mentorRequestCreateRequestDto
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/mentors")
	public ResponseEntity<CommonResponseDto<?>> createMentorRequest(HttpServletRequest request,
		@Valid @ModelAttribute MentorRequestCreateRequestDto mentorRequestCreateRequestDto)
		throws IOException {
		Long userId = jwtProvider.getCookieReturnUserId(request, ACCESS_TOKEN);

		if (fileRepository.findByUserIdAndFileCategory(userId, FileCategory.EMPLOYEE_CARD)
			.isPresent()) {
			fileService.deleteFile(userId, FileCategory.EMPLOYEE_CARD);
		}

		CommonResponseDto<?> fileDto = fileService.createFile(
			mentorRequestCreateRequestDto.getMultipartFile(), userId, FileCategory.EMPLOYEE_CARD);
		ImageFile imageFile = fileRepository.findByUserIdAndFileCategoryOrElseThrow(userId,
			FileCategory.EMPLOYEE_CARD);
		CommonResponseDto<?> response = mentorRequestService.createMentorRequest(userId,
			mentorRequestCreateRequestDto, imageFile);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * 멘토 신청 삭제 API
	 *
	 * @param request
	 * @param mentorRequestId
	 * @return
	 */
	@DeleteMapping("/mentors/{mentorRequestId}")
	public ResponseEntity<CommonResponseDto<?>> deleteMentorRequest(HttpServletRequest request,
		@PathVariable Long mentorRequestId) {
		Long userId = jwtProvider.getCookieReturnUserId(request, ACCESS_TOKEN);

		CommonResponseDto<?> commonResponseDto = mentorRequestService.deleteMentorRequest(userId,
			mentorRequestId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	/**
	 * 멘토 신청 조회 API
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/mentors")
	public ResponseEntity<CommonResponseDto<?>> getMentorRequest(HttpServletRequest request) {
		Long userId = jwtProvider.getCookieReturnUserId(request, ACCESS_TOKEN);

		CommonResponseDto<?> commonResponseDto = mentorRequestService.getMentorRequest(userId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

}
