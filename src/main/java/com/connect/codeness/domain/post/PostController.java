package com.connect.codeness.domain.post;

import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

	private PostService postService;
	private  JwtUtil jwtUtil;

	public PostController(PostService postService, JwtUtil jwtUtil) {
		this.postService = postService;
		this.jwtUtil = jwtUtil;
	}

	// 게시글 생성
	@PostMapping
	public ResponseEntity<CommonResponseDto> createPost(@Valid @RequestBody PostCreateRequestDto dto,
		@RequestHeader("Authorization") String token){

		Long userId = jwtUtil.extractUserId(token);

		CommonResponseDto responseDto = postService.createPost(userId, dto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

}
