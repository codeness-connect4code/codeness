package com.connect.codeness.domain.news;


import com.connect.codeness.global.dto.PaginationResponseDto;
import com.connect.codeness.domain.news.dto.NewsResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;

public interface NewsService {

	//Controller에서 호출하는 함수
	CommonResponseDto<PaginationResponseDto<NewsResponseDto>> findNews(int pageNumber, int pageSize);

}

