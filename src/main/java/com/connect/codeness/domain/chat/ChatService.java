package com.connect.codeness.domain.chat;


import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.domain.chat.dto.ChatMessageDto;
import com.connect.codeness.domain.chat.dto.ChatRoomCreateRequestDto;
import com.connect.codeness.domain.chat.dto.ChatRoomDto;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.entity.CreateTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ChatService {

	CommonResponseDto<List<ChatRoomDto>> getChatRooms(long userId);

	CommonResponseDto sendMessage(Long userId, ChatCreateRequestDto dto);

	CommonResponseDto<List<ChatMessageDto>> getChats(Long userId, String chatRoomId);

	CommonResponseDto createChatRoom(Long userId, ChatRoomCreateRequestDto dto);

	CommonResponseDto deleteChatRoom(Long userId, String chatRoomId);

	@Getter
	@Entity
	class ChatRoomHistory extends CreateTimeEntity {

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


		public ChatRoomHistory() {
		}

		@Builder
		public ChatRoomHistory(User user, String chatRoomId) {
			this.user = user;
			this.chatRoomId = chatRoomId;
		}
	}

	@Repository
	interface ChatRoomHistoryRepository extends JpaRepository<ChatRoomHistory, Long> {


		Boolean existsByChatRoomIdAndUserId(String chatRoomId, Long userId);

		Boolean existsByUserId(Long userId);

		Boolean existsByChatRoomId(String chatRoomId);
	}
}

