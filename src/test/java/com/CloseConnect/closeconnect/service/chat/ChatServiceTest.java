package com.CloseConnect.closeconnect.service.chat;

import com.CloseConnect.closeconnect.dto.chat.ChatDto;
import com.CloseConnect.closeconnect.entity.chat.ChatMessage;
import com.CloseConnect.closeconnect.entity.chat.ChatRoom;
import com.CloseConnect.closeconnect.entity.chat.ChatRoomType;
import com.CloseConnect.closeconnect.entity.chat.Participant;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.global.exception.BusinessException;
import com.CloseConnect.closeconnect.global.exception.ExceptionCode;
import com.CloseConnect.closeconnect.repository.chat.ChatMessageRepository;
import com.CloseConnect.closeconnect.repository.chat.ChatRoomRepository;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("mysql")
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private ChatService chatService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void clean() {
        chatMessageRepository.deleteAll();
        chatRoomRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅 저장 테스트")
    public void saveChatTest() throws Exception {
        //given
        ChatRoom existingRoom = new ChatRoom("testChatRoom", ChatRoomType.PUBLIC, List.of(new Participant("test@test.com", "name")), null, null);

        ChatDto.MessageRequest request = ChatDto.MessageRequest.builder()
                .roomId("testRoomId")
                .senderId("testSenderId")
                .senderName("testSenderName")
                .message("testMessage")
                .build();

        //when
        when(chatRoomRepository.findById("testRoomId")).thenReturn(Optional.of(existingRoom));

        chatService.saveChat(request);

        //then
        verify(chatRoomRepository, Mockito.times(1)).save(existingRoom);

        verify(chatMessageRepository, Mockito.times(1)).save(any(ChatMessage.class));

        verify(messagingTemplate, Mockito.times(1)).convertAndSend(eq("/sub/chat"), any(ChatMessage.class));
    }

    @Test
    @DisplayName("채팅방 만들기 테스트")
    public void createChatRoomTest() throws Exception {
        //given
        String creatorEmail = "creator@test.com";
        String receiverEmail = "receiver@test.com";
        Member creator = new Member("creator", creatorEmail, "creatorOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        Member receiver = new Member("receiver", receiverEmail, "receiverOAuth2Id", AuthProvider.NAVER, Role.USER, "Y");

        ChatDto.RoomRequest request = ChatDto.RoomRequest.builder()
                .name("testRoom")
                .chatRoomType(ChatRoomType.PUBLIC)
                .build();

        ChatRoom existingRoom = new ChatRoom("testChatRoom", ChatRoomType.PUBLIC, List.of(new Participant("test@test.com", "name")), null, null);

        //when
        when(memberRepository.findByEmail(creatorEmail)).thenReturn(Optional.of(creator));
        when(memberRepository.findByEmail(receiverEmail)).thenReturn(Optional.of(receiver));
        when(chatRoomRepository.findByChatRoomTypeAndIsDeletedIsFalseAndParticipantListEmailIn(any(), any())).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(existingRoom);

        ChatDto.RoomResponse response = chatService.createChatRoom(request, creatorEmail);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(existingRoom.getId());
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("이미 대화중인 1:1채팅방이 존재할 시 예외발생")
    public void createChatRoomExceptionTest() throws Exception {
        //given
        String creatorEmail = "creator@test.com";
        String receiverEmail = "receiver@test.com";
        Member creator = new Member("creator", creatorEmail, "creatorOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        Member receiver = new Member("receiver", receiverEmail, "receiverOAuth2Id", AuthProvider.NAVER, Role.USER, "Y");

        ChatDto.RoomRequest request = ChatDto.RoomRequest.builder()
                .name("privateRoom")
                .chatRoomType(ChatRoomType.PRIVATE)
                .build();

        ChatRoom existingRoom = new ChatRoom("privateRoom", ChatRoomType.PRIVATE, List.of(new Participant(creatorEmail, creator.getName()), new Participant(receiverEmail, receiver.getName())), LocalDateTime.now(), null);

        //when
        when(memberRepository.findByEmail(creatorEmail)).thenReturn(Optional.of(creator));
        when(memberRepository.findByEmail(receiverEmail)).thenReturn(Optional.of(receiver));
        when(memberRepository.findByEmail(request.getReceiverEmail())).thenReturn(Optional.of(receiver));
        when(chatRoomRepository.findByChatRoomTypeAndIsDeletedIsFalseAndParticipantListEmailIn(any(), any())).thenReturn(Optional.of(existingRoom));

        //then
        assertThatThrownBy(() -> chatService.createChatRoom(request, creatorEmail))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ExceptionCode.ALREADY_JOINED_ROOM.getMessage());
    }

    @Test
    @DisplayName("채팅방 참가 테스트")
    public void participantRoomTest() throws Exception {
        //given
        String roomId = "testRoomId";
        String email = "test@test.com";
        Member member = new Member("testName", email, "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        ChatRoom chatRoom = new ChatRoom("testRoom", ChatRoomType.PUBLIC, new ArrayList<>(), LocalDateTime.now(), null);

        //when
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
        when(chatRoomRepository.save(any())).thenReturn((chatRoom));

        chatService.participantRoom(roomId, email);

        //then
        verify(chatRoomRepository, times(1)).findById(roomId);
        verify(chatRoomRepository, times(1)).save(chatRoom);

    }

    @Test
    @DisplayName("채팅방 참가 예외발생")
    public void participantRoomExceptionTest() throws Exception {
        //given
        String roomId = "testRoomId";
        String email = "test@test.com";
        Member member = new Member("testName", email, "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        ChatRoom chatRoom = new ChatRoom("testRoom", ChatRoomType.PUBLIC, new ArrayList<>(), LocalDateTime.now(), null);
        chatRoom.addParticipant(new Participant(email, "testName"));

        //when
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));

        BusinessException exception = assertThrows(BusinessException.class, () -> chatService.participantRoom(roomId, email));

        //then
        verify(chatRoomRepository, times(1)).findById(roomId);
        assertThat(ExceptionCode.ALREADY_JOINED_ROOM).isEqualTo(exception.getExceptionCode());
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 나가기 테스트")
    public void outRoomTest() throws Exception {
        //given
        String roomId = "testRoomId";
        String email = "test@test.com";
        Member member = new Member("testName", email, "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        ChatRoom chatRoom = new ChatRoom("testRoom", ChatRoomType.PUBLIC, new ArrayList<>(), LocalDateTime.now(), null);
        chatRoom.addParticipant(new Participant(email, "testName"));

        //when
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
        when(chatRoomRepository.save(any())).thenReturn(chatRoom);

        chatService.outRoom(roomId, email);

        //then
        verify(chatRoomRepository, times(1)).findById(roomId);
        assertThat(chatRoom.getParticipantList().size()).isEqualTo(0);
        assertThat(chatRoom.isDeleted()).isEqualTo(true);
        verify(chatRoomRepository, times(1)).save(chatRoom);
    }

    @Test
    @DisplayName("내가 참가중인 채팅방 리스트 테스트")
    public void myChatRoomListTest() throws Exception {
        //given
        String email = "test@test.com";
        ChatRoom room1 = new ChatRoom("room1", ChatRoomType.PUBLIC, List.of(new Participant(email, "testName")), LocalDateTime.now(), LocalDateTime.now());
        ChatRoom room2 = new ChatRoom("room2", ChatRoomType.PRIVATE, List.of(new Participant(email, "testName")), LocalDateTime.now(), LocalDateTime.now().plusHours(3));
        ChatRoom room3 = new ChatRoom("room3", ChatRoomType.PUBLIC, List.of(new Participant(email, "testName")), LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        List<ChatRoom> chatRoomList = Arrays.asList(room1, room2, room3);

        //when
        when(chatRoomRepository.findByParticipantListEmail(email)).thenReturn(chatRoomList);

        List<ChatDto.RoomResponse> response = chatService.myChatRoomList(email);

        //then
        assertThat(response).isNotEmpty();
        assertThat(response.size()).isEqualTo(3);
        assertThat(response.get(0).getName()).isEqualTo("room2");
        assertThat(response.get(1).getName()).isEqualTo("room3");
    }

    @Test
    @DisplayName("오픈채팅방 리스트 테스트")
    public void openChatRoomListTest() throws Exception {
        //given
        ChatRoom room1 = new ChatRoom("room1", ChatRoomType.PUBLIC, List.of(new Participant("test@test.com", "testName")), LocalDateTime.now(), LocalDateTime.now());
        ChatRoom room2 = new ChatRoom("room2", ChatRoomType.PUBLIC, List.of(new Participant("test2@test.com", "testName2")), LocalDateTime.now(), LocalDateTime.now().plusHours(3));
        ChatRoom room3 = new ChatRoom("room3", ChatRoomType.PUBLIC, List.of(new Participant("test3@test.com", "testName3")), LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        List<ChatRoom> chatRoomList = Arrays.asList(room1, room2, room3);

        //when
        when(chatRoomRepository.findByChatRoomTypeAndIsDeletedFalse(ChatRoomType.PUBLIC)).thenReturn(chatRoomList);

        List<ChatDto.RoomResponse> response = chatService.openChatRoomList();

        //then
        assertThat(response).isNotEmpty();
        assertThat(response.size()).isEqualTo(3);
        assertThat(response.get(0).getName()).isEqualTo("room2");
        assertThat(response.get(2).getName()).isEqualTo("room1");
    }

    @Test
    @DisplayName("채팅메세지 리스트 테스트")
    public void chatMessageListTest() throws Exception {
        //given
        String roomId = "testRoomId";
        ChatMessage message = new ChatMessage(roomId, "testSenderId", "testName", "testMessage", LocalDateTime.now());
        ChatMessage message2 = new ChatMessage(roomId, "testSenderId2", "testName2", "testMessage2", LocalDateTime.now().plusSeconds(5));
        ChatMessage message3 = new ChatMessage(roomId, "testSenderId3", "testName3", "testMessage3", LocalDateTime.now().plusSeconds(3));

        List<ChatMessage> messageList = List.of(message, message2, message3);

        //when
        when(chatMessageRepository.findByRoomIdAndIsDeletedFalse(roomId)).thenReturn(messageList);

        List<ChatDto.MessageResponse> response = chatService.chatMessageList(roomId);

        //then
        assertThat(response).isNotEmpty();
        assertThat(response.size()).isEqualTo(3);
        assertThat(response.get(1).getSenderId()).isEqualTo(message3.getSenderId());
        assertThat(response.get(2).getMessage()).isEqualTo(message2.getMessage());
    }
}


























