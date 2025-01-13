package com.connect.codeness.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomDto {

	private String chatRoomId;

	//상대방 ID
	private Long partnerId;

	//상대방 프로필 이미지 주소
	private String partnerProfileUrl;

	private String lastMessage;

	private final String lastMessageTime;

	private int unreadCount;

	private boolean isActive;

}
