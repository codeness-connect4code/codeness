package com.connect.codeness.domain.user;

import com.connect.codeness.domain.file.ProfileImage;
import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;

@Getter
@Entity(name = "user")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String userNickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus userStatus;

	private String phoneNumber;

	private String region;

	private int career;

	private String mbti;

	private String site_link;

	//사용자 계좌
	private String account;

	private String bankName;

	private String firebaseUserId;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private ProfileImage profileImage;

	public User() {}
}
