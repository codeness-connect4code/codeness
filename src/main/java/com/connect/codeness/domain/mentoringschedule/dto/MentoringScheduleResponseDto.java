package com.connect.codeness.domain.mentoringschedule.dto;

import com.connect.codeness.global.enums.BookedStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentoringScheduleResponseDto {

	private Long id; //멘토링 스케쥴 고유 식별자

	private Long mentoringPostId;//멘토링 공고 고유 식별자

	private LocalDate mentoringDate; //멘토링 날짜

	private LocalTime mentoringTime; //멘토링 시간

	private BookedStatus bookedStatus; //예약 여부

}
