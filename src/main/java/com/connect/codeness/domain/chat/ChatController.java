package com.connect.codeness.domain.chat;

import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.domain.chat.dto.ChatMessageDto;
import com.connect.codeness.domain.chat.dto.ChatRoomDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-rooms")
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	//TODO:나중에 지울 것
	//채팅방 생성
	@PostMapping
	public ResponseEntity<CommonResponseDto> createChatRoom(

	){
		CommonResponseDto responseDto = chatService.createChatRoom(1L, 2L);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	//채팅 보내기
	@PostMapping("/chat")
	public ResponseEntity<CommonResponseDto> sendMessage(
		//TODO: 유저 인증 정보 추가
		@RequestBody ChatCreateRequestDto dto
	) {
		CommonResponseDto commonResponseDto = chatService.sendMessage(1L, dto);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED);
	}


	//채팅방 목록 보여주기
	@GetMapping
	public ResponseEntity<CommonResponseDto<List<ChatRoomDto>>> getChatRooms(
		//TODO: 유저 인증 정보 추가
	) {

		CommonResponseDto<List<ChatRoomDto>> responseDto
			= chatService.getChatRooms(1L);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	//채팅방 상세 조회
	@GetMapping("/{chatRoomId}")
	public ResponseEntity<CommonResponseDto<List<ChatMessageDto>>> getChats(
		//TODO: 유저 인증 정보 추가
		@PathVariable String chatRoomId
	) {
		CommonResponseDto<List<ChatMessageDto>> responseDto
			= chatService.getChats(1L, chatRoomId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}

