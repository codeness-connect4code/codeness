package com.connect.codeness.domain.mentoringpost;

import com.connect.codeness.domain.mentoringschedule.MentoringSchedule;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.entity.CreateTimeEntity;
import com.connect.codeness.global.enums.FieldType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
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

	@OneToMany(mappedBy = "mentoringPost", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<MentoringSchedule> mentoringSchedule = new ArrayList<>();//멘토링 공고 삭제시, 멘토링 스케쥴도 삭제

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FieldType field;//분야

	@Column(nullable = false, length = 30)
	private String title;//공고 제목

	@Column(nullable = false, length = 30)
	private String company;//회사 이름

	@Column(nullable = false)
	private Integer career;//경력

	@Column(nullable = false, length = 30)
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

	@Column(nullable = false, length = 300)
	private String description;//설명글

	public MentoringPost() {

	}

	@Builder
	public MentoringPost(User user, FieldType field, String title, String company, Integer career, String region,
		BigDecimal price, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String description, LocalDateTime createdAt) {
		this.user = user;
		this.field = field;
		this.title = title;
		this.company = company;
		this.career = career;
		this.region = region;
		this.price = price;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
	}

}
