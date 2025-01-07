package com.connect.codeness.domain.mentoringschedule;


import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.BookedStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
@Entity
@Table(name = "mentoring_schedule")
public class MentoringSchedule extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //멘토링  스케쥴 고유 식별자

	@Column(nullable = false)
	private LocalDate mentoringDate; //멘토링 날짜

	@Column(nullable = false)
	private LocalTime mentoringTime; //멘토링 시간

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookedStatus bookedStatus; //예약 여부

}
