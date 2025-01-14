package com.connect.codeness.domain.news.service;


import com.connect.codeness.domain.news.dto.NewsResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.data.domain.Page;

public interface NewsService {

	//Controller에서 호출하는 함수
	CommonResponseDto<Page<NewsResponseDto>> findNews(int pageNumber, int pageSize);

}

