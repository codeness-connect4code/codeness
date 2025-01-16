package com.connect.codeness.domain.admin.dto;

import com.connect.codeness.global.enums.MentorRequestStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUpdateMentorRequestDto {

	private MentorRequestStatus isAccepted;

}
