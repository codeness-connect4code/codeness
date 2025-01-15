package com.connect.codeness.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomDto {

	private String chatRoomId;

	//상대방 ID
	private Long partnerId;

	//상대방 닉네임
	private String partnerNick;

	//상대방 프로필 이미지 주소
	private String partnerProfileUrl;

	private String lastMessage;

	private String lastMessageTime;

	private Integer unreadCount;

	private Boolean isActive;

}
