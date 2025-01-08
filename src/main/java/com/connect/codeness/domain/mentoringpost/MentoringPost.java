package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.field.Field;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.entity.CreateTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
@Entity
@Table(name = "mentoring_post")
public class MentoringPost extends CreateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;//멘토링 공고 고유 식별자

	//연관관계 : 1:1
	@OneToOne
	@JoinColumn(name = "mentor_id")
	private User user;//사용자 고유 식별자 (외래키)

	//연관관계 : N:1
	@ManyToOne
	@JoinColumn(name = "field_id")
	private Field field;//분야 고유 식별자 (외래키)

	@Column(nullable = false)
	private String title;//공고 제목

	@Column(nullable = false)
	private String company;//회사 이름

	@Column(nullable = false)
	private Integer career;//경력

	@Column(nullable = false)
	private String region;//지역

	@Column(nullable = false)
	private BigDecimal price;//가격

	@Column(nullable = false)
	private LocalDate startDate;//멘토링 시작 날짜

	@Column(nullable = false)
	private LocalDate endDate;//멘토링 마감 날짜

	@Column(nullable = false)
	private LocalTime startTime;//멘토링 시작 시간

	@Column(nullable = false)
	private LocalTime endTime;//멘토링 마감 시간

	@Column(nullable = false)
	private String description;//설명글

}
