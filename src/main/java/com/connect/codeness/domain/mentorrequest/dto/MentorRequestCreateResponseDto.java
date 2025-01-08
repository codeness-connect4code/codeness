package com.connect.codeness.domain.mentorrequest.dto;

import com.connect.codeness.global.enums.Field;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class MentorRequestCreateResponseDto {

//	 private MultipartFile employCard;

	@NotNull
	 private String company;

	@NotNull
	 private Field field;

	@NotNull
	 private String phoneNumber;

	@NotNull
	 private String position;

	@NotNull
	 private Integer career;

	@NotNull
	@Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
	 private String companyEmail;

}
