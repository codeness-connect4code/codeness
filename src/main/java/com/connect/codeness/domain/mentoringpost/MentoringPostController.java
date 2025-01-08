package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class MentoringPostController {

	private final MentoringPostService mentoringPostService;

	public MentoringPostController(MentoringPostService mentoringPostService) {
		this.mentoringPostService = mentoringPostService;
	}

	//todo : 파일 service 구현시 파일 추가로 수정 (사원증 이미지 첨부)
	@PostMapping("/mentors")
	public ResponseEntity<CommonResponseDto> createMentoringPost(
		@RequestBody MentoringPostCreateDto mentoringPostCreateDto
	) {}

	

}
