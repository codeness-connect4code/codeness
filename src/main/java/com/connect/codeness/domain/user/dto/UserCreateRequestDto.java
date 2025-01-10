package com.connect.codeness.domain.user.dto;

import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {
	@NotBlank
	@Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String email;

	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8글자 이상이며, 영문, 숫자, 특수문자를 1개씩 포함해야합니다.")
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String password;

	@NotBlank
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String name;

	@NotBlank
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String nickname;

	@NotBlank
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String phoneNumber;

	@NotBlank
	private FieldType field;

	@NotBlank
	private UserRole userRole;
}
