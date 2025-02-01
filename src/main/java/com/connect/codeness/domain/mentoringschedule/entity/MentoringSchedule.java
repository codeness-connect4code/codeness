package com.connect.codeness.domain.mentoringschedule.entity;


import com.connect.codeness.domain.mentoringpost.entity.MentoringPost;
import com.connect.codeness.global.entity.CreateTimeEntity;
import com.connect.codeness.global.enums.BookedStatus;
import com.connect.codeness.global.enums.MentoringScheduleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "mentoring_schedule")
public class MentoringSchedule extends CreateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //멘토링 스케쥴 고유 식별자

	@ManyToOne
	@JoinColumn(name = "mentoring_post_id")
	private MentoringPost mentoringPost;//멘토링 공고 고유 식별자

	@Column(nullable = false)
	private LocalDate mentoringDate; //멘토링 날짜

	@Column(nullable = false)
	private LocalTime mentoringTime; //멘토링 시간

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookedStatus bookedStatus; //예약 여부

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MentoringScheduleStatus mentoringScheduleStatus;//멘토링 스케쥴 삭제 여부

	public MentoringSchedule() {

	}

	/**
	 * 결제 스케쥴 상태 변경
	 * - BOOKED -> EMPTY
	 * - BOOKED -> IN_PROGRESS
	 */
	public void updateBookedStatus(BookedStatus bookedStatus) {
		this.bookedStatus = bookedStatus;
	}

	/**
	 * 멘토링 공고 생성 - 스케쥴 생성
	 */
	@Builder
	public MentoringSchedule(MentoringPost mentoringPost, LocalDate mentoringDate, LocalTime mentoringTime,
		BookedStatus bookedStatus, MentoringScheduleStatus mentoringScheduleStatus) {
		this.mentoringPost = mentoringPost;
		this.mentoringDate = mentoringDate;
		this.mentoringTime = mentoringTime;
		this.bookedStatus = bookedStatus;
		this.mentoringScheduleStatus = mentoringScheduleStatus;
	}

	/**
	 * 멘토링 공고 삭제시 멘토링 공고 스케쥴 상태 변경
	 */
	public void updateStatus(MentoringScheduleStatus mentoringScheduleStatus) {
		this.mentoringScheduleStatus = mentoringScheduleStatus;
	}
}
