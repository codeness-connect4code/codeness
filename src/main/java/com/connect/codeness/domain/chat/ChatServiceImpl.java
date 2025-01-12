package com.connect.codeness.domain.chat;

import com.connect.codeness.domain.chat.dto.ChatResponseDto;
import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.domain.chat.dto.MessageDto;
import com.connect.codeness.domain.chat.repository.ChatRoomUserRepository;
import com.connect.codeness.domain.user.User;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

	private final FirebaseDatabase firebaseDatabase;
	private final UserRepository userRepository;
	private final ChatRoomUserRepository chatRoomUserRepository;

	public ChatServiceImpl(FirebaseDatabase firebaseDatabase, UserRepository userRepository, ChatRoomUserRepository chatRoomUserRepository
		) {
		this.firebaseDatabase = firebaseDatabase;
		this.userRepository = userRepository;
		this.chatRoomUserRepository = chatRoomUserRepository;
	}



	//채팅방 생성


	//파이어베이스 DB에서 내 채팅방 정보 가져오기
	@Override
	public CommonResponseDto<List<ChatResponseDto>> findChatRooms(long userId) {
		User user = userRepository.findByIdOrElseThrow(userId);

//		List<ChatResponseDto> responseList = chatRoomUserRepository.findChatListWithOtherUserProfile(
//			user);
//
//		return CommonResponseDto.<List<ChatResponseDto>>builder()
//			.msg("내 채팅방 조회가 완료되었습니다.")
//			.data(responseList)
//			.build();
		return null;
	}

	@Override
	public CommonResponseDto sendMessage(Long userId, ChatCreateRequestDto dto) {
		DatabaseReference reference = firebaseDatabase.getReference()
			.child("chatMessages")
			.child(dto.getFirebaseChatRoomId());

		MessageDto message = MessageDto.builder()
			.senderId(userId)
			.content(dto.getMessage())
			.timestamp(System.currentTimeMillis()).build();

		reference.push().setValue(message, (error, ref) ->{
			if(error == null){
				log.info("{} -> {} : {} 메시지 저장 성공",userId,dto.getFirebaseChatRoomId(), System.currentTimeMillis());
			}else {
				log.info("{} -> {} : {} 메시지 저장 실패",userId,dto.getFirebaseChatRoomId(), System.currentTimeMillis());
			}
		});

		return CommonResponseDto.builder().msg("채팅 전송 완료").build();
	}

	//파이어 베이스에서 채팅 가져오기

}

