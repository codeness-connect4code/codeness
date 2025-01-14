package com.connect.codeness.domain.admin;

import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;

import com.connect.codeness.domain.admin.dto.AdminUpdateMentorRequestDto;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private final AdminService adminService;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public AdminController(AdminService adminService, JwtUtil jwtUtil,
		UserRepository userRepository) {
		this.adminService = adminService;
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@GetMapping("/mentors")
	public ResponseEntity<CommonResponseDto> getMentors(
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize
	){
		CommonResponseDto<Page<UserResponseDto>> responseDto =
			adminService.getMentors(pageNumber, pageSize);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@GetMapping("/mentors/{mentorId}")
	public ResponseEntity<CommonResponseDto> getMentor(
		@PathVariable Long mentorId
	){
		CommonResponseDto commonResponseDto = adminService.getMentor(mentorId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	@GetMapping("/mentors/mentor-requests")
	public ResponseEntity<CommonResponseDto> getMentorRequests(
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize
	){
		CommonResponseDto<Page<MentorRequestResponseDto>> commonResponseDto = adminService.getMentorRequests(pageNumber, pageSize);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}


	@GetMapping("/mentors/mentor-requests/{mentorRequestId}")
	public ResponseEntity<CommonResponseDto> getMentorRequest(
		@PathVariable Long mentorRequestId
	){
		CommonResponseDto<MentorRequestResponseDto> commonResponseDto = adminService.getMentorRequest(
			mentorRequestId);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

	@PatchMapping("/mentors/mentor-requests/{mentorRequestId}")
	public ResponseEntity<CommonResponseDto> updateMentor(
		@PathVariable Long mentorRequestId, @RequestBody AdminUpdateMentorRequestDto adminUpdateMentorRequestDto
	){
		CommonResponseDto commonResponseDto = adminService.updateMentor(mentorRequestId, adminUpdateMentorRequestDto);
		return new ResponseEntity<>(commonResponseDto, HttpStatus.OK);
	}

}
