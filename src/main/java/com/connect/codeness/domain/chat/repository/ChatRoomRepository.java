package com.connect.codeness.domain.chat.repository;

import com.connect.codeness.domain.chat.entity.ChatRoom;
import com.connect.codeness.domain.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
