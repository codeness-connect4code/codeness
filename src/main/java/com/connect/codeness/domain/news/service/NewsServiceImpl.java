package com.connect.codeness.domain.news.service;

import com.connect.codeness.domain.news.dto.NewsResponseDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NewsServiceImpl implements NewsService {

	private final NewsCacheService cacheService;

	public NewsServiceImpl(NewsCacheService cacheService) {
		this.cacheService = cacheService;
	}


	@Override
	public CommonResponseDto<Page<NewsResponseDto>> findNews(int pageNumber, int pageSize) {
		try {
			Long[] allNewsIds = cacheService.fetchTopStoryIds();

			if (allNewsIds == null || allNewsIds.length == 0) {
				throw new BusinessException(ExceptionType.NOT_FOUND_NEWS);
			}

			int start = pageNumber * pageSize;

			if (start >= allNewsIds.length) {
				throw new BusinessException(ExceptionType.NOT_FOUND_NEWS);
			}

			List<NewsResponseDto> newsList = Arrays.stream(allNewsIds)
				.skip(start)
				.limit(pageSize)
				.map(id -> {
					try {
						return cacheService.fetchStory(id);
					} catch (Exception e) {
						log.error("Error fetching story id: {}", id, e);
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toList();

			PageImpl<NewsResponseDto> newsPage = new PageImpl<>(newsList,
				PageRequest.of(pageNumber, pageSize, Sort.by("time")), allNewsIds.length);

			return CommonResponseDto.<Page<NewsResponseDto>>builder()
				.msg("뉴스 조회가 완료되었습니다.")
				.data(newsPage)
				.build();

		} catch (Exception e) {
			log.error("Error finding news", e);
			throw new BusinessException(ExceptionType.NOT_FOUND_NEWS);
		}
	}
}