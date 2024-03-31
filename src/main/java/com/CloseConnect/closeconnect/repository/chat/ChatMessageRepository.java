package com.CloseConnect.closeconnect.repository.chat;

import com.CloseConnect.closeconnect.entity.chat.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
