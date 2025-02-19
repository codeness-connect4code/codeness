package com.connect.codeness.domain.comment.entity;


import com.connect.codeness.domain.post.entity.Post;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.entity.BaseEntity;
import com.connect.codeness.global.enums.CommentStatus;
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

	@Column
	private String writerProfileUrl;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CommentStatus commentStatus;

	public void inputWriterProfileUrl(String writerProfileUrl){
		this.writerProfileUrl = writerProfileUrl;
	}

	public void updateComment(String content){
		this.content = content;
	}

	public Comment() {
	}

	public void deleteComment() {
		this.commentStatus = CommentStatus.DELETED;
	}

	@Builder
	public Comment(User user, Post post, String content, String writerProfileUrl, CommentStatus commentStatus) {
		this.user = user;
		this.post = post;
		this.content = content;
		this.writerProfileUrl = writerProfileUrl;
		this.commentStatus = commentStatus;
	}
}
