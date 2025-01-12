package com.connect.codeness.domain.chat;


import com.connect.codeness.domain.chat.dto.ChatResponseDto;
import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;

public interface ChatService {

	CommonResponseDto<List<ChatResponseDto>> findChatRooms(long userId);

	CommonResponseDto sendMessage(Long userId, ChatCreateRequestDto dto);
}

