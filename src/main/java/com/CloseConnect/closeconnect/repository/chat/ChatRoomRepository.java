package com.CloseConnect.closeconnect.repository.chat;

import com.CloseConnect.closeconnect.entity.chat.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByParticipantListEmail(String email);

    List<ChatRoom> findByIsDeletedTrue();
}
