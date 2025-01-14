package com.connect.codeness.domain.chat.repository;

import com.connect.codeness.domain.chat.entity.ChatRoomHistory;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomHistoryRepository extends JpaRepository<ChatRoomHistory, Long> {

	Optional<ChatRoomHistory> findByChatRoomIdAndUserId(String chatRoomId, Long userId);

	default void findByChatRoomAndUserOrElseThrow(String chatRoomId, Long userId){
		findByChatRoomIdAndUserId(chatRoomId, userId).orElseThrow(
			() -> new BusinessException(ExceptionType.UNAUTHORIZED_DELETE_REQUEST)
		);
	}
}
