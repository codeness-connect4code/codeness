package com.connect.codeness.domain.chat.repository;

import com.connect.codeness.domain.chat.entity.ChatRoomHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomHistoryRepository extends JpaRepository<ChatRoomHistory, Long> {


	Boolean existsByChatRoomIdAndUserId(String chatRoomId, Long userId);

	Boolean existsByUserId(Long userId);

	Boolean existsByChatRoomId(String chatRoomId);

	@Query("SELECT c.chatRoomId FROM ChatRoomHistory c WHERE c.user.id = :userId")
	List<String> findChatRoomIdByUserId(@Param("userId") Long userId);
}
