package com.connect.codeness.domain.mentorrequest;

import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.entity.CreateTimeEntity;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.MentorRequestStatus;
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
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "mentor_request")
public class MentorRequest extends CreateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String company;

	@Column(nullable = false)
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String phoneNumber;

	@Column(nullable = false)
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String position;

	@Column(nullable = false)
	private Integer career;

	@Column(nullable = false)
	@Size(max = 30, message = "이 필드는 최대 {max}자까지 가능합니다.")
	private String companyEmail;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MentorRequestStatus isAccepted;

	//todo : file service 구현 시 추가 수정
//	@Column(nullable = false)
//	private String employeeCardUrl;

	@Enumerated(EnumType.STRING)
	private FieldType field;

	@Builder
	public MentorRequest(
		User user, String company, String phoneNumber,
		String position, Integer career, String companyEmail,
		MentorRequestStatus isAccepted, FieldType field
		) {
		this.user = user;
		this.company = company;
		this.phoneNumber = phoneNumber;
		this.position = position;
		this.career = career;
		this.companyEmail = companyEmail;
		this.isAccepted = isAccepted;
		this.field = field;
	}

	public MentorRequest() {}

}
