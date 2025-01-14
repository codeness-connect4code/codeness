package com.connect.codeness.domain.admin;

import static com.connect.codeness.global.constants.Constants.AUTHORIZATION;
import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;

import com.connect.codeness.domain.user.dto.UserResponseDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.UserRole;
import org.hibernate.usertype.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private final AdminService adminService;
	private final JwtUtil jwtUtil;

	public AdminController(AdminService adminService, JwtUtil jwtUtil) {
		this.adminService = adminService;
		this.jwtUtil = jwtUtil;
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

}
