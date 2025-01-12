package com.connect.codeness.domain.post;

import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;

import com.connect.codeness.domain.post.dto.PostCreateRequestDto;
import com.connect.codeness.domain.post.dto.PostResearchResponseDto;
import com.connect.codeness.global.Jwt.JwtUtil;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.PostType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	// 게시글 전체
	@GetMapping
	public ResponseEntity<CommonResponseDto<Page<PostResearchResponseDto>>> getAllPosts(
		@RequestParam(required = false) PostType postType,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) String writer,
		@RequestParam(required = false, defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

		CommonResponseDto<Page<PostResearchResponseDto>> posts = postService.findAllPost(postType, keyword, writer, pageable);

		return new ResponseEntity<>(posts, HttpStatus.OK);
	}
}
