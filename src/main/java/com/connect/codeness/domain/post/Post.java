package com.connect.codeness.domain.post;

import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.CommunityStatus;
import com.connect.codeness.global.enums.PostType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Long count;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PostType Type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CommunityStatus Status;

	public Post() {
	}
}
