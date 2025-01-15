package com.connect.codeness.domain.post;

import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.PostStatus;
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
import jakarta.validation.constraints.Size;
import lombok.Builder;
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
	@Size(max = 50)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String writer;

	@Column(nullable = false)
	private Long view;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PostType postType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PostStatus postStatus;

	@Builder
	public Post(User user, String title, String content, String writer, Long view, PostType postType,
		PostStatus postStatus) {
		this.user = user;
		this.title = title;
		this.writer = writer;
		this.content = content;
		this.view = view;
		this.postType = postType;
		this.postStatus = postStatus;
	}

	public void increaseView(Long view) {
		this.view = view+ 1L;
	}

	public void updatePost(String title, String content){
		this.title = title;
		this.content = content;
	}

	public void deletePost(){
		this.postStatus = PostStatus.DELETED;
	}

	public Post() {
	}
}
