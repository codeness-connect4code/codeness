package com.connect.codeness.domain.user.dto;

import com.connect.codeness.global.dto.CommonResponseDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtResponseDto {
	private String token;
	private CommonResponseDto dto;

}
