package com.connect.codeness.domain.news.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsResponseDto {
	private Long id;
	private String title;
	private String by;
	private String time;
	private String url;
	//
	private boolean deleted;
}
