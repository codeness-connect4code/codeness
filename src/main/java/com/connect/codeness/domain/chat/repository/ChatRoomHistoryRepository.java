package com.connect.codeness.domain.chat.repository;

import com.connect.codeness.domain.chat.entity.ChatRoomHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomHistoryRepository extends JpaRepository<ChatRoomHistory, Long> {


	Boolean existsByChatRoomIdAndUserId(String chatRoomId, Long userId);

	Boolean existsByUserId(Long userId);

	Boolean existsByChatRoomId(String chatRoomId);
}
