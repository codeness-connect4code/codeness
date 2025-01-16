package com.connect.codeness.domain.mentoringpost.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaginationResponseDto<T> {

	private List<T> content; //현재 페이지 data
	private int totalPages; //총 페이지 개수
	private long totalElements; //총 데이터 개수
	private int pageNumber; //현재 페이지 번호
	private int pageSize; //페이지 크기

}
