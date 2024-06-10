package com.CloseConnect.closeconnect.repository.chat;

import com.CloseConnect.closeconnect.entity.chat.ChatRoom;
import com.CloseConnect.closeconnect.entity.chat.ChatRoomType;
import com.CloseConnect.closeconnect.entity.chat.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ChatRoomRepositoryTest {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @BeforeEach
    void clean() {
        chatRoomRepository.deleteAll();
    }

    @Test
    public void findByParticipantListEmailTest() throws Exception {
        //given
        String email = "test@test.com";
        ChatRoom chatRoom = ChatRoom.builder()
                .name("testChatRoom")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant(email, "testName"), new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
                .name("testChatRoom2")
                .chatRoomType(ChatRoomType.PRIVATE)
                .participantList(List.of(new Participant(email, "testName"), new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
                .name("testChatRoom3")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();

        chatRoomRepository.saveAll(List.of(chatRoom, chatRoom2, chatRoom3));

        //when
        List<ChatRoom> response = chatRoomRepository.findByParticipantListEmail(email);

        //then
        assertThat(response.size()).isEqualTo(2);
    }

    @Test
    public void findByIsDeletedTrueTest() throws Exception {
        //given
        ChatRoom chatRoom = ChatRoom.builder()
                .name("testChatRoom")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant("test@test.com", "testName"), new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
                .name("testChatRoom2")
                .chatRoomType(ChatRoomType.PRIVATE)
                .participantList(List.of(new Participant("test@test.com", "testName"), new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
                .name("testChatRoom3")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        chatRoom.delete();

        chatRoomRepository.saveAll(List.of(chatRoom, chatRoom2, chatRoom3));
        //when
        List<ChatRoom> response = chatRoomRepository.findByIsDeletedTrue();

        //then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getName()).isEqualTo("testChatRoom");
    }

    @Test
    public void findByChatRoomTypeAndIsDeletedFalse() throws Exception {
        //given
        ChatRoom chatRoom = ChatRoom.builder()
                .name("testChatRoom")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant("test@test.com", "testName"), new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
                .name("testChatRoom2")
                .chatRoomType(ChatRoomType.PRIVATE)
                .participantList(List.of(new Participant("test@test.com", "testName"), new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
                .name("testChatRoom3")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant("test@test.com", "testName"), new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom4 = ChatRoom.builder()
                .name("testChatRoom4")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant("test@test.com", "testName"), new Participant("test2@test.com", "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();

        chatRoom.delete();
        chatRoom3.delete();

        chatRoomRepository.saveAll(List.of(chatRoom, chatRoom2, chatRoom3, chatRoom4));
        //when
        List<ChatRoom> response = chatRoomRepository.findByChatRoomTypeAndIsDeletedFalse(ChatRoomType.PRIVATE);

        //then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getName()).isEqualTo("testChatRoom2");
    }
    
    @Test
    public void findByChatRoomTypeAndIsDeletedIsFalseAndParticipantListEmailInTest() throws Exception {
        String email = "test@test.com";
        String email2 = "test2@test.com";
        //given
        ChatRoom chatRoom = ChatRoom.builder()
                .name("testChatRoom")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant(email, "testName"), new Participant(email2, "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom2 = ChatRoom.builder()
                .name("testChatRoom2")
                .chatRoomType(ChatRoomType.PRIVATE)
                .participantList(List.of(new Participant(email, "testName"), new Participant(email2, "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom3 = ChatRoom.builder()
                .name("testChatRoom3")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant("test3@test.com", "testName3"), new Participant(email2, "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        ChatRoom chatRoom4 = ChatRoom.builder()
                .name("testChatRoom4")
                .chatRoomType(ChatRoomType.PUBLIC)
                .participantList(List.of(new Participant("test4@test.com", "testName4"), new Participant(email2, "testName2")))
                .createdTime(null)
                .lastChatTime(null)
                .build();
        chatRoomRepository.saveAll(List.of(chatRoom, chatRoom2, chatRoom3, chatRoom4));

        //when
        Optional<ChatRoom> response =
                chatRoomRepository.findByChatRoomTypeAndIsDeletedIsFalseAndParticipantListEmailIn(ChatRoomType.PRIVATE, List.of(email, email2));

        //then
        assertThat(response).isPresent();
        assertThat(response.get().getName()).isEqualTo("testChatRoom2");
    }
}














