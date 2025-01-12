package com.connect.codeness.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatResponseDto {

	private Long chatRoomId;

	//상대방 ID
	private Long partnerId;

	//상대방 프로필 이미지 주소
	private String partnerUrl;

	private String lastMessage;

	private final LocalDateTime lastMessageTime;

	private int unReadCount;


	@Builder
	public ChatResponseDto(Long chatRoomId, String lastMessage,
		int unReadCount, LocalDateTime lastMessageTime, String profileUrl){
		this.chatRoomId = chatRoomId;
		this.lastMessage = lastMessage;
		this.partnerUrl = profileUrl;
		this.unReadCount = unReadCount;
		this.lastMessageTime = lastMessageTime;
	}
}
