package com.connect.codeness.domain.post;

import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	// 게시글 생성
	@PostMapping
	public ResponseEntity<CommonResponseDto> createPost(@RequestBody PostCreateRequestDto dto){

		CommonResponseDto responseDto = postService.createPost(dto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

}
