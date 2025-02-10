package com.connect.codeness.domain.chat.entity;

import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.entity.CreateTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class ChatRoomHistory extends CreateTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(nullable = false, length = 100)
	private String chatRoomId;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime expireAt;


	public ChatRoomHistory() {
	}

	@Builder
	public ChatRoomHistory(User user, LocalDateTime expireAt, String chatRoomId) {
		this.user = user;
		this.expireAt = expireAt;
		this.chatRoomId = chatRoomId;
	}

	public void updateExpireAt(LocalDateTime expireAt){
		this.expireAt = expireAt;
	}
}
