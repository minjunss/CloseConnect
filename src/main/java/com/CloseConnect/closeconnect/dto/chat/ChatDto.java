package com.CloseConnect.closeconnect.dto.chat;

import com.CloseConnect.closeconnect.entity.chat.Participant;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ChatDto {

    @Data
    @NoArgsConstructor
    public static class Message {
        private String roomId;
        private String senderId;
        private String message;
        private LocalDateTime time = LocalDateTime.now();

        @Builder
        public Message(String roomId, String senderId, String message) {
            this.roomId = roomId;
            this.senderId = senderId;
            this.message = message;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Room {
        private String name;
        private List<Participant> participantList = new ArrayList<>();
        private LocalDateTime createdTime = LocalDateTime.now();

        public Room(String name) {
            this.name = name;
        }
    }
}
