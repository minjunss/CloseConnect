package com.CloseConnect.closeconnect.entity.chat;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_message")
@NoArgsConstructor
@Getter
public class ChatMessage {
    @Id
    private String id;
    private String roomId;
    private String senderId;
    private String senderName;
    private String message;
    private LocalDateTime time;
    private boolean isDeleted;

    @Builder
    public ChatMessage(String roomId, String senderId, String senderName, String message, LocalDateTime time) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.time = time;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
