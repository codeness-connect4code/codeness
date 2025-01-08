package com.connect.codeness.domain.user.dto;

import com.connect.codeness.global.enums.Field;
import com.connect.codeness.global.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {
	@NotBlank(message = "이메일을 입력해 주세요.")
	@Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
	private String email;

	@NotBlank(message = "비밀번호를 입력해 주세요.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8글자 이상이며, 영문, 숫자, 특수문자를 1개씩 포함해야합니다.")
	private String password;

	private String name;

	private String nickname;

	private String phoneNumber;

	private Field field;

	private UserRole userRole;
}
