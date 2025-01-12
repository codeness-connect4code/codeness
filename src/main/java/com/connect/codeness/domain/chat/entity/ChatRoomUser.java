package com.connect.codeness.domain.chat.entity;

import com.connect.codeness.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class ChatRoomUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	private int unreadCount;

	private LocalDateTime lastReadTime;

	public ChatRoomUser() { }

	@Builder
	public ChatRoomUser(User user, ChatRoom chatRoom){
		this.user = user;
		this.chatRoom = chatRoom;
	}

	public void updateUnreadCount(int count){
		this.unreadCount = count;
	}

	public void updateLastReadTime(){
		this.lastReadTime = LocalDateTime.now();
	}

}
