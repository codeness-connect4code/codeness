package com.connect.codeness.domain.chat.controller;

import static com.connect.codeness.global.constants.Constants.ACCESS_TOKEN;

import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.domain.chat.dto.ChatMessageDto;
import com.connect.codeness.domain.chat.dto.ChatRoomCreateRequestDto;
import com.connect.codeness.domain.chat.dto.ChatRoomDto;
import com.connect.codeness.domain.chat.service.ChatService;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	private final JwtProvider jwtProvider;

	public ChatController(ChatService chatService, JwtProvider jwtProvider) {
		this.chatService = chatService;
		this.jwtProvider = jwtProvider;
	}

	//TODO:나중에 지울 것
	//채팅방 생성
	@PostMapping
	public ResponseEntity<CommonResponseDto> createChatRoom(
		HttpServletRequest request,
		@RequestBody ChatRoomCreateRequestDto dto
	) {
		Long userId = jwtProvider.getCookieReturnUserId(request,ACCESS_TOKEN);
		CommonResponseDto responseDto = chatService.createChatRoom(userId, dto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	//채팅 보내기
	@PostMapping("/chat")
	public ResponseEntity<CommonResponseDto> sendMessage(
		HttpServletRequest request,
		@RequestBody ChatCreateRequestDto dto
	) {
		Long userId = jwtProvider.getCookieReturnUserId(request,ACCESS_TOKEN);

		CommonResponseDto commonResponseDto = chatService.sendMessage(userId, dto);

		return new ResponseEntity<>(commonResponseDto, HttpStatus.CREATED);
	}


	//채팅방 목록 보여주기
	@GetMapping
	public ResponseEntity<CommonResponseDto<List<ChatRoomDto>>> getChatRooms(
		HttpServletRequest request
	) {
		Long userId = jwtProvider.getCookieReturnUserId(request,ACCESS_TOKEN);

		CommonResponseDto<List<ChatRoomDto>> responseDto
			= chatService.getChatRooms(userId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	//채팅방 상세 조회
	@GetMapping("/{chatRoomId}")
	public ResponseEntity<CommonResponseDto<List<ChatMessageDto>>> getChats(
		HttpServletRequest request,
		@PathVariable String chatRoomId
	) {
		Long userId = jwtProvider.getCookieReturnUserId(request,ACCESS_TOKEN);

		CommonResponseDto<List<ChatMessageDto>> responseDto
			= chatService.getChats(userId, chatRoomId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@DeleteMapping("/{chatRoomId}")
	public ResponseEntity<CommonResponseDto> deleteChatRoom(
		HttpServletRequest request,
		@PathVariable String chatRoomId
	) {

		Long userId = jwtProvider.getCookieReturnUserId(request,ACCESS_TOKEN);

		CommonResponseDto responseDto = chatService.deleteChatRoom(userId, chatRoomId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}

