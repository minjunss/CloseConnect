package com.CloseConnect.closeconnect.repository.chat;

import com.CloseConnect.closeconnect.entity.chat.ChatRoom;
import com.CloseConnect.closeconnect.entity.chat.ChatRoomType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByParticipantListEmail(String email);

    List<ChatRoom> findByIsDeletedTrue();
    List<ChatRoom> findByIsDeletedFalse();

    Optional<ChatRoom> findByChatRoomTypeAndIsDeletedIsFalseAndParticipantListEmailIn(ChatRoomType chatRoomType, List<String> emails);
}
