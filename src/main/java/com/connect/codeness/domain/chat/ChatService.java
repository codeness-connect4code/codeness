package com.connect.codeness.domain.chat;


import com.connect.codeness.domain.chat.dto.ChatMessageDto;
import com.connect.codeness.domain.chat.dto.ChatRoomDto;
import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;

public interface ChatService {

	CommonResponseDto<List<ChatRoomDto>> getChatRooms(long userId);

	CommonResponseDto sendMessage(Long userId, ChatCreateRequestDto dto);

	CommonResponseDto<List<ChatMessageDto>> getChats(Long userId, String chatRoomId);

	CommonResponseDto createChatRoom(Long userId, ChatRoomCreateRequestDto dto);

	CommonResponseDto deleteChatRoom(Long userId, String chatRoomId);
}

