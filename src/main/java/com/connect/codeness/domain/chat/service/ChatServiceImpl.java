package com.connect.codeness.domain.chat.service;

import static com.connect.codeness.global.constants.Constants.AUTO_DELETE_WAITING;
import static com.connect.codeness.global.constants.Constants.CHATROOMS_LOAD_TIME;
import static com.connect.codeness.global.constants.Constants.CHATS_LOAD_TIME;
import static com.connect.codeness.global.constants.Constants.RETENTION_DAYS;
import static com.connect.codeness.global.constants.Constants.SCHEDULE_TIME;

import com.connect.codeness.domain.chat.dto.ChatCreateRequestDto;
import com.connect.codeness.domain.chat.dto.ChatMessageDto;
import com.connect.codeness.domain.chat.dto.ChatRoomCreateRequestDto;
import com.connect.codeness.domain.chat.dto.ChatRoomDto;
import com.connect.codeness.domain.chat.entity.ChatRoomHistory;
import com.connect.codeness.domain.chat.repository.ChatRoomHistoryRepository;
import com.connect.codeness.domain.file.entity.ImageFile;
import com.connect.codeness.domain.paymenthistory.entity.PaymentHistory;
import com.connect.codeness.domain.paymenthistory.repository.PaymentHistoryRepository;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.domain.user.repository.UserRepository;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.enums.UserRole;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

	private final FirebaseDatabase firebaseDatabase;
	private final UserRepository userRepository;
	private final ChatRoomHistoryRepository chatRoomHistoryRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private static final int BATCH_SIZE = 1000;

	public ChatServiceImpl(FirebaseDatabase firebaseDatabase,
		UserRepository userRepository, ChatRoomHistoryRepository chatRoomHistoryRepository,
		PaymentHistoryRepository paymentHistoryRepository
	) {
		this.firebaseDatabase = firebaseDatabase;
		this.userRepository = userRepository;
		this.chatRoomHistoryRepository = chatRoomHistoryRepository;
		this.paymentHistoryRepository = paymentHistoryRepository;
	}


	//채팅방 생성
	@Transactional
	@Override
	public CommonResponseDto<?> createChatRoom(Long myId, ChatRoomCreateRequestDto dto) {

		//멘토인 경우 채팅방 생성 불가 & 유저 정보 가져오기(나)
		User myInfo = userRepository.findByIdOrElseThrow(myId);

		if(myInfo.getRole() == UserRole.MENTOR){
			throw new BusinessException(ExceptionType.UNAUTHORIZED_POST_REQUEST);
		}

		//결제내역이 없는 경우, 채팅방 생성 불가
		PaymentHistory paymentHistory = paymentHistoryRepository.findByIdOrElseThrow(dto.getPaymentHistoryId());

		if(!Objects.equals(paymentHistory.getPayment().getUser().getId(), myId)){
			throw new BusinessException(ExceptionType.UNAUTHORIZED_POST_REQUEST);
		}


		//채팅방ID를 생성한다.(유저 ID가 각각 1,2 이면 -> 1_2)
		Long partnerId = paymentHistory.getUser().getId();
		String chatRoomId = generateChatRoomId(myId, partnerId);

		//생성된 채팅방 ID가 이미 존재하는지 확인
		if (chatRoomHistoryRepository.existsByChatRoomId(chatRoomId)) {

			chatRoomHistoryRepository.findByChatRoomId(chatRoomId)
				.forEach(chatRoom -> chatRoom.updateExpireAt(getExpireAt(dto)));
		}

		//유저 정보 가져오기(상대방)
		User partnerInfo = userRepository.findByIdOrElseThrow(partnerId);

		//프로필 파일 가져오기(내 사진, 상대방 사진)
		ImageFile myImageFile = findProfileImage(myInfo);
		ImageFile partnerImageFile = findProfileImage(partnerInfo);

		//파일 경로 추출(프로필 주소)
		String myProfileUrl = myImageFile == null ? null : myImageFile.getFilePath();
		String partnerProfileUrl = partnerImageFile == null ? null : partnerImageFile.getFilePath();

		//채팅방에 넣을 데이터(내 채팅방 데이터, 상대방 채팅방 데이터)
		ChatRoomDto myData = ChatRoomDto.builder()
			.partnerId(partnerId)
			.partnerProfileUrl(partnerProfileUrl)
			.unreadCount(0)
			.isActive(true)
			.partnerNick(partnerInfo.getUserNickname())
			.build();

		ChatRoomDto partnerData = ChatRoomDto.builder()
			.partnerId(myId)
			.partnerProfileUrl(myProfileUrl)
			.unreadCount(0)
			.isActive(true)
			.partnerNick(myInfo.getUserNickname())
			.build();

		//파이어베이스의 참조 노드 설정
		DatabaseReference Ref = firebaseDatabase.getReference("chatRooms");

		//내 채팅방, 상대 채팅방 정보 파이어베이스에 저장 ex) 내ID가 1이면 1-> 1_2 -> 채팅방 정보
		Ref.child(String.valueOf(myId)).child(chatRoomId).setValueAsync(myData);
		Ref.child(String.valueOf(partnerId)).child(chatRoomId).setValueAsync(partnerData);

		//채팅방 이력에 저장(나, 상대방)
		ChatRoomHistory myChatRoomHistory = createChatRoomHistory(myInfo, chatRoomId, dto);
		ChatRoomHistory partnerChatRoomHistory = createChatRoomHistory(partnerInfo, chatRoomId, dto);

		chatRoomHistoryRepository.save(myChatRoomHistory);
		chatRoomHistoryRepository.save(partnerChatRoomHistory);

		//응답 반환
		return CommonResponseDto.builder().msg("채팅방 생성 완료").build();
	}


	//채팅 전송(비동기 처리)
	@Override
	public CommonResponseDto<?> sendMessage(Long userId, ChatCreateRequestDto dto) {
		//내가 전송할 권한이 없으면 예외 처리
		checkAuthorizationOrElseThrow(userId, dto.getFirebaseChatRoomId(), ExceptionType.UNAUTHORIZED_POST_REQUEST);

		//참조 노드 설정
		DatabaseReference reference = firebaseDatabase.getReference(String.format("chatMessages/%s", dto.getFirebaseChatRoomId()));

		//TODO: 욕설 처리 로직 추가하기

		//보낼 메시지 생성
		ChatMessageDto message = ChatMessageDto.builder()
			.senderId(userId)
			.content(dto.getMessage())
			.timestamp(LocalDateTime.now().toString())
			.build();

		//메시지 보내기
		reference.push().setValue(message, (databaseError, databaseReference) -> {
			if (databaseError != null) {
				log.info("Message: {} at {} could not be sent {}",
					dto.getMessage(), LocalDateTime.now(), databaseError.getMessage());
			} else {
				log.info("Message Successfully sent!");
				updateChatRoomInfoWhenSend(dto.getFirebaseChatRoomId(), message, userId);
			}
		});

		return CommonResponseDto.builder().msg("채팅 전송 완료").build();
	}

	//내 채팅방 목록 조회
	@Override
	public CommonResponseDto<List<ChatRoomDto>> getChatRooms(long userId) {
		//내가 조회할 권한이 없으면 예외 처리
		checkAuthorizationOrElseThrow(userId, ExceptionType.UNAUTHORIZED_GET_REQUEST);

		//변경된 프로필과 별명 가져오기
		updateChatRoomsInfoWhenGet(userId);

		//참조 노드 설정
		DatabaseReference roomReference = firebaseDatabase.getReference(String.format("chatRooms/%s", userId));

		//CompletableFuture 생성 -> 조회하는 로직이 비동기 처리라서 동기 처리로 바꾸기 위해 추가
		CompletableFuture<List<ChatRoomDto>> future = new CompletableFuture<>();

		//조회를 위한 이벤트리스너 설정
		roomReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				List<ChatRoomDto> myChatRooms = new ArrayList<>();
				for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
					Boolean isActive = roomSnapshot.child("isActive").getValue(Boolean.class);
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
							.partnerNick(roomSnapshot.child("partnerNick").getValue(String.class))
							.build();
						myChatRooms.add(myChatRoom);
					}
				}
				//결과를 myChatRooms에 담음.
				future.complete(myChatRooms);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				log.error("사용자 ID: {}의 채팅방 정보를 불러오지 못했습니다.", userId);
				future.completeExceptionally(new BusinessException(ExceptionType.LOAD_FAIL_CHATROOMLIST));
			}
		});

		try {
			// 데이터를 기다림 (최대 5초)
			List<ChatRoomDto> myChatRooms = future.get(CHATROOMS_LOAD_TIME, TimeUnit.SECONDS);

			log.info("사용자 ID: {}의 채팅방 정보 조회에 성공했습니다.", userId);

			return CommonResponseDto.<List<ChatRoomDto>>builder()
				.data(myChatRooms)
				.msg("채팅방 정보 조회에 성공했습니다.")
				.build();

		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ExceptionType.TIME_OUT_ERROR);

		}
	}

	//채팅방 상세 조회
	@Override
	public CommonResponseDto<List<ChatMessageDto>> getChats(Long userId, String chatRoomId) {
		//내가 조회할 권한이 없으면 예외 처리
		checkAuthorizationOrElseThrow(userId, chatRoomId, ExceptionType.UNAUTHORIZED_GET_REQUEST);

		//참조 노드 설정
		DatabaseReference chatReference = firebaseDatabase.getReference(String.format("chatMessages/%s", chatRoomId));

		//CompletableFuture 생성 -> 조회하는 로직이 비동기 처리라서 동기 처리로 바꾸기 위해 추가
		CompletableFuture<List<ChatMessageDto>> future = new CompletableFuture<>();

		chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				List<ChatMessageDto> myChats = new ArrayList<>();

				for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
					ChatMessageDto myChat = ChatMessageDto.builder()
						.senderId(chatSnapshot.child("senderId").getValue(Long.class))
						.content(chatSnapshot.child("content").getValue(String.class))
						.timestamp(chatSnapshot.child("timestamp").getValue(String.class))
						.build();
					myChats.add(myChat);
				}
				future.complete(myChats);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				log.error("채팅방 ID: {}의 채팅 정보를 불러오지 못했습니다.", chatRoomId);
				future.completeExceptionally(new BusinessException(ExceptionType.LOAD_FAIL_CHATLIST));
			}
		});

		try {
			List<ChatMessageDto> myChats = future.get(CHATS_LOAD_TIME, TimeUnit.SECONDS);

			//채팅방에서의 나의 읽지 않은 메시지 갯수 초기화 -> 0
			updateChatRoomInfoWhenGet(userId, chatRoomId);

			log.info("채팅방 ID: {}의 채팅 정보 조회에 성공했습니다.", chatRoomId);

			return CommonResponseDto.<List<ChatMessageDto>>builder()
				.data(myChats)
				.msg("채팅 정보 조회에 성공했습니다.")
				.build();

		} catch (ExecutionException | InterruptedException | TimeoutException e) {
			throw new BusinessException(ExceptionType.TIME_OUT_ERROR);
		}
	}


	//채팅방 삭제
	@Override
	public CommonResponseDto<?> deleteChatRoom(Long userId, String chatRoomId) {
		//내가 삭제할 권한이 없으면 예외 처리
		//TODO: 삭제는 나중에 서비스 단에서만 제공하기 때문에 굳이 예외처리를 안해줘도 되긴 함.
		checkAuthorizationOrElseThrow(userId, chatRoomId, ExceptionType.UNAUTHORIZED_DELETE_REQUEST);

		//예를 들어, 1번 유저와 2번 유저가 있을 때
		//1번 유저의 1_2와 2번 유저의 1_2의 isActive 상태를 둘다 false로 바꿔야함.
		String[] users = chatRoomId.split("_");

		//참조 노드 설정
		DatabaseReference chatRooms = firebaseDatabase.getReference().child("chatRooms");
		//변경할 데이터 맵
		Map<String, Object> usersStatus = new HashMap<>();
		//
		usersStatus.put("%s/%s/isActive".formatted(users[0], chatRoomId), Boolean.FALSE);
		usersStatus.put("%s/%s/isActive".formatted(users[1], chatRoomId), Boolean.FALSE);

		chatRooms.updateChildrenAsync(usersStatus);

		return CommonResponseDto.builder().msg("채팅방이 삭제되었습니다.").build();
	}

	//만료된 채팅방 자동 삭제(soft-delete)
	@Override
	@Scheduled(cron = "0 0 2 * * *")
	public void autoCleanupExpiredRooms() {
		log.info("--만료일이 지난 채팅방 삭제 시작--");
		LocalDateTime now = LocalDateTime.now();

		int totalProcessed = 0;
		int page = 0;

		try{
			while(true) {
				//삭제될 채팅방 로깅
				Page<ChatRoomHistory> expiredRooms = chatRoomHistoryRepository.findExpiredRooms(now, PageRequest.of(page, BATCH_SIZE));

				if(expiredRooms.isEmpty()){
					break;
				}

				//채팅방 삭제 -> 파이어베이스 DB isActive -> false
				for (ChatRoomHistory expiredRoom : expiredRooms.getContent()) {

					deleteChatRoom(expiredRoom.getUser().getId(),expiredRoom.getChatRoomId());
				}

				totalProcessed += expiredRooms.getNumberOfElements();

				log.info("처리된 배치 번호 {} : {} 개수", page+1, expiredRooms.getNumberOfElements());

				page++;
				Thread.sleep(AUTO_DELETE_WAITING);
			}

			log.info("삭제 완료. 총 처리된 개수: {}",totalProcessed);
		} catch (Exception e) {
			log.error("삭제 도중 발생 에러: ", e);
		}
	}


	//채팅을 보냈을 때 마지막 메시지 & 메시지 시간 & 상대방 안 읽은 메시지 수 + 1
	private void updateChatRoomInfoWhenSend(String chatRoomId, ChatMessageDto message, Long senderId) {

		String[] split = chatRoomId.split("_");
		String partnerId = (Objects.equals(split[0], String.valueOf(senderId))) ? split[1] : split[0];

		//나의 참조 노드 설정
		DatabaseReference myRoomRef = firebaseDatabase.getReference()
			.child("chatRooms")
			.child(String.valueOf(senderId))
			.child(chatRoomId);

		//상대방의 참조 노드 설정
		DatabaseReference partnerRoomRef = firebaseDatabase.getReference()
			.child("chatRooms")
			.child(partnerId)
			.child(chatRoomId);

		//내 변경 데이터
		Map<String, Object> myUpdates = new HashMap<>();
		myUpdates.put("lastMessage", message.getContent());
		myUpdates.put("lastMessageTime", message.getTimestamp());
		myRoomRef.updateChildrenAsync(myUpdates);

		//상대방 변경 데이터(조회한 개수 + 1)
		partnerRoomRef.addListenerForSingleValueEvent(
			new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					long unreadCount = dataSnapshot.child("unreadCount").getValue(Integer.class);

					Map<String, Object> partnerUpdates = new HashMap<>();
					partnerUpdates.put("lastMessage", message.getContent());
					partnerUpdates.put("lastMessageTime", message.getTimestamp());
					partnerUpdates.put("unreadCount", unreadCount + 1);

					partnerRoomRef.updateChildrenAsync(partnerUpdates);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					log.info("파트너 ID: {}의 안 읽은 메시지 수 변경에 실패했습니다.", partnerId);
				}
			}
		);
	}

	//유저 정보 변경 시, 상대방의 프로필 주소와 별명을 다시 세팅(파이어베이스 DB)
	private void updateChatRoomsInfoWhenGet(Long userId) {

		DatabaseReference Ref = firebaseDatabase.getReference("chatRooms");

		List<String> list = chatRoomHistoryRepository.findChatRoomIdByUserId(userId);

		//채팅방 정보 업데이트
		Map<String, Object> updates = new HashMap<>();

		for (String chatRoomId : list) {
			String[] Ids = chatRoomId.split("_");

			String partnerId = Ids[0].equals(String.valueOf(userId)) ? Ids[1] : Ids[0];

			User partner = userRepository.findByIdOrElseThrow(Long.valueOf(partnerId));

			String partnerNick = partner.getUserNickname();
			String partnerUrl = null;

			ImageFile partnerImage = partner.getImageFiles()
				.stream()
				.filter(imageFile -> imageFile.getFileCategory() == FileCategory.PROFILE)
				.findFirst().orElse(null);

			if(partnerImage != null){
				partnerUrl = partnerImage.getFilePath();
			}

			updates.put("partnerProfileUrl",partnerUrl);
			updates.put("partnerNick",partnerNick);

			Ref.child(String.valueOf(userId)).child(chatRoomId).updateChildrenAsync(updates);
			updates.clear();
		}
	}

	//채팅방을 상세 조회 할 때, 안 읽은 메시지수 -> 0
	private void updateChatRoomInfoWhenGet(Long userId, String chatRoomId) {

		DatabaseReference myRoomRef = firebaseDatabase.getReference(String.format("chatRooms/%s/%s", userId, chatRoomId));

		//내 채팅방 정보 업데이트
		Map<String, Object> myUpdates = new HashMap<>();
		myUpdates.put("unreadCount", 0);

		myRoomRef.updateChildrenAsync(myUpdates);
	}

	//채팅방 ID 생성
	public String generateChatRoomId(Long userId, Long partnerId) {
		return String.format("%d_%d", userId, partnerId);
	}

	//내 이미지 가져오기 -> 없으면 null 있으면 값 반환
	private ImageFile findProfileImage(User user) {
		return user.getImageFiles()
			.stream()
			.filter(image -> image.getFileCategory() == FileCategory.PROFILE)
			.findFirst()
			.orElse(null);
	}

	//ChatRoomHistory 객체 생성
	private ChatRoomHistory createChatRoomHistory(User myInfo, String chatRoomId, ChatRoomCreateRequestDto dto) {

		LocalDateTime expireAt = getExpireAt(dto);

		return ChatRoomHistory.builder()
			.user(myInfo)
			.chatRoomId(chatRoomId)
			.expireAt(expireAt)
			.build();
	}

	//권한 체크(채팅 전송, 채팅방 상세 조회, 채팅방 삭제)
	private void checkAuthorizationOrElseThrow(Long userId, String chatRoomId, ExceptionType exType) {

		if (!chatRoomHistoryRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
			throw new BusinessException(exType);
		}
	}

	//권한 체크(채팅방 목록 조회)
	private void checkAuthorizationOrElseThrow(Long userId, ExceptionType exType) {

		if (!chatRoomHistoryRepository.existsByUserId(userId)) {
			throw new BusinessException(exType);
		}
	}

	//채팅방 만료시간 얻기
	private static LocalDateTime getExpireAt(ChatRoomCreateRequestDto dto) {
		return LocalDateTime.of(dto.getMentoringDate(), dto.getMentoringTime())
			.plusHours(SCHEDULE_TIME)
			.plusDays(RETENTION_DAYS);
	}
}

