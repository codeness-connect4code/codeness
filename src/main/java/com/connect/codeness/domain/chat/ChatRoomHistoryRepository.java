package com.connect.codeness.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ChatRoomHistoryRepository extends JpaRepository<ChatRoomHistory, Long> {


	Boolean existsByChatRoomIdAndUserId(String chatRoomId, Long userId);

	Boolean existsByUserId(Long userId);

	Boolean existsByChatRoomId(String chatRoomId);
}
