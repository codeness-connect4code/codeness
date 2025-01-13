package com.connect.codeness.domain.chat;

import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.domain.chat.dto.ChatMessageDto;
import com.connect.codeness.domain.chat.dto.ChatRoomDto;
import com.connect.codeness.domain.chat.repository.ChatRoomUserRepository;
import com.connect.codeness.domain.user.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

	private final FirebaseDatabase firebaseDatabase;
	private final UserRepository userRepository;

	public ChatServiceImpl(FirebaseDatabase firebaseDatabase, UserRepository userRepository, ChatRoomUserRepository chatRoomUserRepository
		) {
		this.firebaseDatabase = firebaseDatabase;
		this.userRepository = userRepository;
	}


	//채팅방 생성

	//채팅 보내기
	@Override
	public CommonResponseDto sendMessage(Long userId, ChatCreateRequestDto dto) {
		DatabaseReference reference = firebaseDatabase.getReference()
			.child("chatMessages")
			.child(dto.getFirebaseChatRoomId());

		//TODO: 욕설 처리 로직 추가하기
		ChatMessageDto message = ChatMessageDto.builder()
			.senderId(userId)
			.content(dto.getMessage())
			.timestamp(LocalDateTime.now().toString()).build();

		reference.push().setValue(message, (error, ref) ->{
			if(error == null){
				log.info("{} -> {} : {} 메시지 저장 성공",userId,dto.getFirebaseChatRoomId(), System.currentTimeMillis());
				// 마지막 메시지 및 읽지 않은 메시지 수 업데이트
				updateChatRoomInfoWhenSend(dto.getFirebaseChatRoomId(), message, userId);
			}else {
				log.info("{} -> {} : {} 메시지 저장 실패",userId,dto.getFirebaseChatRoomId(), System.currentTimeMillis());
			}
		});

		return CommonResponseDto.builder().msg("채팅 전송 완료").build();
	}

	//파이어베이스 DB에서 내 채팅방 정보 가져오기
	@Override
	public CommonResponseDto<List<ChatRoomDto>> getChatRooms(long userId) {
		DatabaseReference roomReference = firebaseDatabase.getReference()
			.child("chatRooms")
			.child(String.valueOf(userId));

		CompletableFuture<List<ChatRoomDto>> future = new CompletableFuture<>();

		roomReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				List<ChatRoomDto> myChatRooms = new ArrayList<>();
				for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
					Boolean isActive = roomSnapshot.child("active").getValue(Boolean.class);
					if (isActive) {
						ChatRoomDto myChatRoom = ChatRoomDto.builder()
							.chatRoomId(roomSnapshot.getKey())
							.partnerId(roomSnapshot.child("partnerId").getValue(Long.class))
							.partnerProfileUrl(
								roomSnapshot.child("partnerProfileUrl").getValue(String.class))
							.lastMessage(roomSnapshot.child("lastMessage").getValue(String.class))
							.lastMessageTime(
								roomSnapshot.child("lastMessageTime").getValue(String.class))
							.unreadCount(roomSnapshot.child("unreadCount").getValue(Integer.class))
							.isActive(roomSnapshot.child("active").getValue(Boolean.class))
							.build();
						myChatRooms.add(myChatRoom);
					}
				}
				future.complete(myChatRooms);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				log.error("사용자 ID: {}의 채팅방 정보를 불러오지 못했습니다.", userId);
				future.completeExceptionally(new BusinessException(ExceptionType.LOAD_FAIL_CHATROOMLIST));
			}
		});

		try {
			List<ChatRoomDto> myChatRooms = future.get(10, TimeUnit.SECONDS);
			return CommonResponseDto.<List<ChatRoomDto>>builder()
				.data(myChatRooms)
				.msg("채팅방 정보 조회에 성공했습니다.")
				.build();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ExceptionType.LOAD_FAIL_CHATROOMLIST);
		}
	}

	//채팅방에 있는 데이터 가져오기
	@Override
	public CommonResponseDto<List<ChatMessageDto>> getChats(Long userId, String chatRoomId) {
		DatabaseReference chatReference = firebaseDatabase.getReference()
			.child("chatMessages")
			.child(chatRoomId);

		//TODO: 내가 들어간 채팅방의 unreadCount 0으로 초기화


		CountDownLatch latch = new CountDownLatch(1);
		List<ChatMessageDto> myChats = new ArrayList<>();

		chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
					ChatMessageDto myChat = ChatMessageDto.builder()
						.senderId(chatSnapshot.child("senderId").getValue(Long.class))
						.content(chatSnapshot.child("content").getValue(String.class))
						.timestamp(chatSnapshot.child("timestamp").getValue(String.class))
						.build();

					myChats.add(myChat);
				}
				latch.countDown();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				latch.countDown();
				log.error("채팅방 ID: {}의 채팅 정보를 불러오지 못했습니다.", chatRoomId);
				throw new BusinessException(ExceptionType.LOAD_FAIL_CHATLIST);
			}
		});

		try {
			if (!latch.await(5, TimeUnit.SECONDS)) {  // 데이터를 기다림
				throw new BusinessException(ExceptionType.LOAD_FAIL_CHATLIST);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ExceptionType.LOAD_FAIL_CHATLIST);
		}
		//채팅방에서의 나의 읽지 않은 메시지 갯수 초기화 -> 0
		updateChatRoomInfoWhenGet(userId, chatRoomId);


		return CommonResponseDto.<List<ChatMessageDto>>builder()
			.data(myChats)
			.msg("채팅방 정보 조회에 성공했습니다.")
			.build();
	}

	@Override
	public CommonResponseDto createChatRoom(Long myId, Long partnerId) {
		//데이터 소스를 가져온다.
		DatabaseReference myReference = firebaseDatabase.getReference("chatRooms")
			.child(String.valueOf(myId));

		DatabaseReference partnerReference = firebaseDatabase.getReference("chatRooms")
			.child(String.valueOf(partnerId));

		//채팅방ID를 생성한다.
		String chatRoomId = generateChatRoomId(myId, partnerId);

//		//내 이미지 가져오기
//		ImageFile myProfileUrl = userRepository.findByIdOrElseThrow(myId).getImageFiles().stream()
//			.filter(
//				imageFile -> imageFile.getFileCategory() == FileCategory.PROFILE
//			).findFirst().orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_FILE));
//
//		//상대방 이미지 가져오기
//		ImageFile partnerProfileUrl = userRepository.findByIdOrElseThrow(partnerId).getImageFiles().stream()
//			.filter(
//				imageFile -> imageFile.getFileCategory() == FileCategory.PROFILE
//			).findFirst().orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_FILE));


		//내 채팅방 구조
		Map<String, Object> myChatRoomData = new HashMap<>();
		Map<String, Object> partnerChatRoomData = new HashMap<>();

		myChatRoomData.put(chatRoomId, ChatRoomDto.builder()
			.partnerId(partnerId)
			.partnerProfileUrl("https://example.com/partner_profile.jpg")
			.unreadCount(0)
			.isActive(true).build());


		partnerChatRoomData.put(chatRoomId, ChatRoomDto.builder()
			.partnerId(myId)
			.partnerProfileUrl("https://example.com/my_profile.jpg")
			.unreadCount(0)
			.isActive(true).build());


		myReference.setValueAsync(myChatRoomData);
		partnerReference.setValueAsync(partnerChatRoomData);

		return CommonResponseDto.builder().msg("채팅방 생성 완료").build();
	}

	@Override
	public CommonResponseDto deleteChatRoom(String chatRoomId) {
		//예를 들어, 1번 유저와 2번 유저가 있을 때
		//1번 유저의 1_2와 2번 유저의 1_2의 isActive 상태를 둘다 false로 바꿔야함.
		String[] users = chatRoomId.split("_");

		DatabaseReference chatRooms = firebaseDatabase.getReference().child("chatRooms");
		Map<String, Object> usersStatus = new HashMap<>();

		usersStatus.put("%s/%s/active".formatted(users[0],chatRoomId), Boolean.FALSE);
		usersStatus.put("%s/%s/active".formatted(users[1],chatRoomId), Boolean.FALSE);

		chatRooms.updateChildrenAsync(usersStatus);

		return CommonResponseDto.builder().msg("채팅방이 삭제되었습니다.").build();
	}

//	private String convertToLocalDateTime(long time){
//		ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
//
//		return Instant.ofEpochMilli(time)
//			.atZone(koreaZoneId)
//			.toLocalDateTime().toString();
//	}


	private void updateChatRoomInfoWhenSend(String chatRoomId, ChatMessageDto message, Long senderId) {

		String[] split = chatRoomId.split("_");
		String partnerId = (Objects.equals(split[0], String.valueOf(senderId))) ? split[1] : split[0];

		DatabaseReference myRoomRef = firebaseDatabase.getReference()
			.child("chatRooms")
			.child(String.valueOf(senderId))
			.child(chatRoomId);

		DatabaseReference partnerRoomRef = firebaseDatabase.getReference()
			.child("chatRooms")
			.child(partnerId)
			.child(chatRoomId);

		//내 채팅방 정보 업데이트
		Map<String, Object> myUpdates = new HashMap<>();
		myUpdates.put("lastMessage", message.getContent());
		myUpdates.put("lastMessageTime", message.getTimestamp());

		//상대 채팅방 정보 업데이트
		partnerRoomRef.addListenerForSingleValueEvent(
			new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					long unreadCount = dataSnapshot.hasChild("unreadCount") ? dataSnapshot.child("unreadCount").getValue(Long.class) : 0;

					Map<String, Object> partnerUpdates = new HashMap<>();
					partnerUpdates.put("lastMessage", message.getContent());
					partnerUpdates.put("lastMessageTime", message.getTimestamp());
					partnerUpdates.put("unreadCount", unreadCount + 1);

					partnerRoomRef.updateChildrenAsync(partnerUpdates);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					log.info("파트너ID: {}의 안 읽은 메시지 수 변경에 실패했습니다.",partnerId);
				}
			}
		);

		myRoomRef.updateChildrenAsync(myUpdates);
	}

	private void updateChatRoomInfoWhenGet(Long userId, String chatRoomId) {

		DatabaseReference myRoomRef = firebaseDatabase.getReference()
			.child("chatRooms")
			.child(String.valueOf(userId))
			.child(chatRoomId);

		//내 채팅방 정보 업데이트
		Map<String, Object> myUpdates = new HashMap<>();
		myUpdates.put("unreadCount", 0);

		myRoomRef.updateChildrenAsync(myUpdates);
	}

	private String  generateChatRoomId(Long userId, Long partnerId){
		return String.format("%d_%d",userId,partnerId);
	}
}

