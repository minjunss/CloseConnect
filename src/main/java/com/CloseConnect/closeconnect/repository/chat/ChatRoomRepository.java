package com.CloseConnect.closeconnect.repository.chat;

import com.CloseConnect.closeconnect.entity.chat.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
}
