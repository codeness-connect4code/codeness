package com.connect.codeness.domain.comment;


import com.connect.codeness.domain.post.Post;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.CommunityStatus;
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
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(nullable = false)
	@Size(max = 100)
	private String content;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CommunityStatus status;

	public Comment() {
	}

	@Builder
	public Comment(User user, Post post, String content, CommunityStatus status) {
		this.user = user;
		this.post = post;
		this.content = content;
		this.status = status;
	}
}
