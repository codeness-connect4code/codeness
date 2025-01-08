package com.connect.codeness.domain.mentorrequest.dto;

import com.connect.codeness.global.enums.FieldType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentorRequestCreateResponseDto {

//	 private MultipartFile employCard;

	@NotNull
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	 private String company;

	@NotNull
	 private FieldType field;

	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	@NotNull
	 private String phoneNumber;

	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	@NotNull
	 private String position;

	@NotNull
	 private Integer career;

	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	@NotNull
	@Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
	 private String companyEmail;

}
