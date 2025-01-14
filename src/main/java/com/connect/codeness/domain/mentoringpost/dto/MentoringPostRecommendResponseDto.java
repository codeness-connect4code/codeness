package com.connect.codeness.domain.mentoringpost.dto;

import com.connect.codeness.domain.mentoringpost.MentoringPost;
import com.connect.codeness.global.enums.FieldType;
import lombok.Getter;

@Getter
public class MentoringPostRecommendResponseDto {

	private Long mentoringPostId;
	private String title;
	private FieldType field;
	private Integer career;
	private String userNickname;
	private Double starRating;

	public MentoringPostRecommendResponseDto(MentoringPost post, Double starRating){
		this.mentoringPostId = post.getId();
		this.title = post.getTitle();
		this.field = post.getField();
		this.career = post.getCareer();
		this.userNickname = post.getUser().getUserNickname();
		this.starRating = starRating;
	}
}
