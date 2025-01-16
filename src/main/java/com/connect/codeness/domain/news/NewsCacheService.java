package com.connect.codeness.domain.news;

import com.connect.codeness.domain.news.dto.NewsResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class NewsCacheService {

	private final RestTemplate restTemplate;
	private final String BASE_URL = "https://hacker-news.firebaseio.com/v0";  // https로 변경
	private final String SUB_URL = "newstories.json";

	public NewsCacheService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Cacheable(value = "newStories", key = "'newStories'", unless = "#result == null")
	public Long[] fetchTopStoryIds() {
		try {
			String url = String.format("%s//%s", BASE_URL, SUB_URL);
			return restTemplate.getForObject(url, Long[].class);
		} catch (Exception e) {
			log.error("Error fetching top story ids", e);
			return new Long[0];
		}
	}

	@Cacheable(value = "stories", key = "#id.toString()", unless = "#result == null")
	public NewsResponseDto fetchStory(Long id) {
		log.info("Calling API for story ID: {}", id);
		try {
			String url = String.format("%s//%s//%d.json", BASE_URL, "item", id);
			JsonNode response = restTemplate.getForObject(url, JsonNode.class);

			if (response == null || response.has("deleted")) {
				return null;
			}

			// 필드 존재 여부 확인
			Long storyId = response.has("id") ? response.get("id").asLong() : null;
			String title = response.has("title") ? response.get("title").asText() : "";
			String by = response.has("by") ? response.get("by").asText() : "";
			String newsUrl = response.has("url") ? response.get("url").asText() : "";

			if (storyId == null || title.isEmpty()) {
				return null;
			}

			// 시간 변환
			LocalDateTime dateTime = LocalDateTime.ofEpochSecond(
				response.get("time").asLong(), 0, ZoneOffset.UTC);
			String time = dateTime.atZone(ZoneId.of("Asia/Seoul"))
				.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));

			return NewsResponseDto.builder()
				.id(storyId)
				.title(title)
				.by(by)
				.time(time)
				.url(newsUrl)
				.build();

		} catch (Exception e) {
			log.error("Error fetching story id: {}", id, e);
			return null;
		}
	}
}
