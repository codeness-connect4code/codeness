package com.connect.codeness.domain.chat.entity;


import com.connect.codeness.global.entity.CreateTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class ChatRoom extends CreateTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String firebaseChatRoomId;

	@Column(length = 300)
	private String lastMessage;

	private LocalDateTime lastMessageTime;

	private LocalDateTime deletedAt;

	public ChatRoom() {
	}

	@Builder
	public ChatRoom(String firebaseChatRoomId) {

		this.firebaseChatRoomId = firebaseChatRoomId;

	}

	public void updateLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public void updateLastMessageTime() {
		this.lastMessageTime = LocalDateTime.now();
	}

	public void deleteChatRoom(){
		this.deletedAt = LocalDateTime.now();
	}
}
