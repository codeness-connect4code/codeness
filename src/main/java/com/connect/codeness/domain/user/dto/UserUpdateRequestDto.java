package com.connect.codeness.domain.user.dto;

import com.connect.codeness.global.enums.FieldType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
public class UserUpdateRequestDto {
	private String nickname;
	private String phoneNumber;
	private String region;
	private FieldType field;
	private Integer career = 0;
	private String mbti;
	private String siteLink;
	private MultipartFile multipartFile;

}
