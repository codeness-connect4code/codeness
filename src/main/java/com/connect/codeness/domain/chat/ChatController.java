package com.connect.codeness.domain.chat;

import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-rooms")
public class ChatController {

	private ChatService chatService;

	public ChatController(ChatService chatService){
		this.chatService = chatService;
	}

	//채팅 보내기
	@PostMapping("/chat")
	public ResponseEntity<CommonResponseDto> sendMessage(
		@RequestBody ChatCreateRequestDto dto
	){
		CommonResponseDto commonResponseDto = chatService.sendMessage(1L, dto);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED);
	}


//	@GetMapping
//	public ResponseEntity<CommonResponseDto<List<ChatResponseDto>>> findChatRooms(
//		//TODO: 유저 인증 정보 추가
//	){
//
//		CommonResponseDto<List<ChatResponseDto>> responseDto
//			= chatService.findChatRooms(1L);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.OK);
//	}
//
//	@GetMapping("/{chatRoomId}")
//	public ResponseEntity<CommonResponseDto<List<ChatResponseDetailDto>>> findChats(
//		@RequestParam Long chatRoomId
//	){
//
//		CommonResponseDto<List<ChatResponseDetailDto>> responseDto
//			= chatService.findChats(chatRoomId);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.OK);
//	}
}
