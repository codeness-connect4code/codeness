package com.connect.codeness.domain.admin;

import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@PostMapping
	public ResponseEntity<CommonResponseDto> test(){
		CommonResponseDto dto = CommonResponseDto.builder().msg("success").build();
		return ResponseEntity.ok(dto);
	}

}
