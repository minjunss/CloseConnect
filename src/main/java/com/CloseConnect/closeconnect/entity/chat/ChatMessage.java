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
    private String message;
    private LocalDateTime time;

    @Builder
    public ChatMessage(String roomId, String senderId, String message, LocalDateTime time) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.message = message;
        this.time = time;
    }
}
