package com.connect.codeness.domain.chatroom;


import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class ChatRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String firebaseChatRoomId;

	@ManyToOne
	@JoinColumn(name = "mentor_id")
	private User mentor;

	@ManyToOne
	@JoinColumn(name = "mentee_id")
	private User mentee;

	public ChatRoom() {
	}

	public ChatRoom(String firebaseChatRoomId, User mentor, User mentee) {

		this.firebaseChatRoomId = firebaseChatRoomId;

		this.mentor = mentor;

		this.mentee = mentee;
	}

}
