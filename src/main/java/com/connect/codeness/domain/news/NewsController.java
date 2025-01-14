package com.connect.codeness.domain.news;

import static com.connect.codeness.global.constants.Constants.PAGE_NUMBER;
import static com.connect.codeness.global.constants.Constants.PAGE_SIZE;

import com.connect.codeness.domain.news.dto.NewsResponseDto;
import com.connect.codeness.domain.news.service.NewsService;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {

	private final NewsService newsService;

	public NewsController(NewsService newsService) {
		this.newsService = newsService;
	}

	@GetMapping
	public ResponseEntity<CommonResponseDto<Page<NewsResponseDto>>> findNews(
		@RequestParam(defaultValue = PAGE_NUMBER) int pageNumber,
		@RequestParam(defaultValue = PAGE_SIZE) int pageSize
	) {
		CommonResponseDto<Page<NewsResponseDto>> responseDto
			= newsService.findNews(pageNumber, pageSize);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
