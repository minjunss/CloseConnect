package com.CloseConnect.closeconnect.service.chat;

import com.CloseConnect.closeconnect.dto.chat.ChatDto;
import com.CloseConnect.closeconnect.entity.chat.ChatMessage;
import com.CloseConnect.closeconnect.entity.chat.ChatRoom;
import com.CloseConnect.closeconnect.entity.chat.Participant;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.repository.chat.ChatMessageRepository;
import com.CloseConnect.closeconnect.repository.chat.ChatRoomRepository;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public void saveChat(ChatDto.MessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 채팅방"));

        ChatMessage chatMessage = ChatMessage.builder()
                .message(request.getMessage())
                .roomId(request.getRoomId())
                .senderId(request.getSenderId())
                .time(request.getTime())
                .build();
        chatRoom.setLastChatTime(chatMessage.getTime());
        chatMessageRepository.save(chatMessage);
        chatRoomRepository.save(chatRoom);

    }

    public void createChatRoom(ChatDto.RoomRequest request, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자 email: " + email));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getName())
                .participantList(Collections.singletonList(new Participant(member.getEmail(), member.getName())))
                .createdTime(request.getCreatedTime())
                .lastChatTime(request.getLastChatTime())
                .build();
        chatRoomRepository.save(chatRoom);
    }

    public void participantRoom(String roomId, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자 email: " + email));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 ChatRoom id: " + roomId));
        Participant participant = new Participant(member.getEmail(), member.getName());

        boolean existParticipant = isExistParticipant(chatRoom, participant);

        if (!existParticipant) {
            chatRoom.addParticipant(participant);
            chatRoomRepository.save(chatRoom);
        } else {
            throw new IllegalStateException("이미 참여중인 방");
        }
    }

    //TODO: 채팅방 리스트(검색조건포함), 채팅삭제, 방 삭제(방의채팅들 삭제), 채팅삭제여부만들고 배치로 일주일지난 삭제채팅은 db에서 삭제
    private boolean isExistParticipant(ChatRoom chatRoom, Participant participant) {
        return chatRoom.getParticipantList().stream().anyMatch(existingParticipant -> existingParticipant.getEmail().equals(participant.getEmail()));
    }

    public void outRoom(String roomId, String email) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 채팅방 id: " + roomId));

        chatRoom.getParticipantList().removeIf(participant -> participant.getEmail().equals(email));
        if (chatRoom.getParticipantList().isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        } else {
            chatRoomRepository.save(chatRoom);
        }
        ;
    }

    public List<ChatDto.RoomResponse> myChatRoomList(String email) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findByParticipantListEmail(email);
        List<ChatDto.RoomResponse> response = chatRoomList.stream().map(
                chatRoom -> ChatDto.RoomResponse.builder()
                        .id(chatRoom.getId())
                        .name(chatRoom.getName())
                        .createdTime(chatRoom.getCreatedTime())
                        .build())
                .sorted(Comparator.comparing(ChatDto.RoomResponse::getCreatedTime))
                .toList();

        return response;
    }

}
