package com.CloseConnect.closeconnect.dto.chat;

import com.CloseConnect.closeconnect.entity.chat.ChatRoomType;
import com.CloseConnect.closeconnect.entity.chat.Participant;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ChatDto {

    @Data
    @NoArgsConstructor
    public static class MessageRequest {
        private String roomId;
        private String senderId;
        private String senderName;
        private String message;
        private LocalDateTime time = LocalDateTime.now();

        @Builder
        public MessageRequest(String roomId, String senderId, String senderName, String message) {
            this.roomId = roomId;
            this.senderId = senderId;
            this.senderName = senderName;
            this.message = message;
        }
    }

    @Data
    @NoArgsConstructor
    public static class MessageResponse {
        private String id;
        private String roomId;
        private String senderId;
        private String senderName;
        private String message;
        private String time;

        @Builder
        public MessageResponse(String id, String roomId, String senderId, String senderName, String message, LocalDateTime time) {
            this.id = id;
            this.roomId = roomId;
            this.senderId = senderId;
            this.senderName = senderName;
            this.message = message;
            this.time = time != null ? time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    @Data
    @NoArgsConstructor
    public static class RoomRequest {
        private String name;
        private ChatRoomType chatRoomType;
        private String receiverEmail;
        private List<Participant> participantList = new ArrayList<>();
        private LocalDateTime createdTime = LocalDateTime.now();
        private LocalDateTime lastChatTime = LocalDateTime.now();

        @Builder
        public RoomRequest(String name, ChatRoomType chatRoomType) {
            this.name = name;
            this.chatRoomType = chatRoomType;
        }
    }

    @Getter
    public static class RoomResponse {
        private final String id;
        private final String name;
        private final ChatRoomType chatRoomType;
        private final List<Participant> participantList;
        private final String createdTime;
        private final String lastChatTime;

        @Builder
        public RoomResponse (String id, String name, ChatRoomType chatRoomType, List<Participant> participantList, LocalDateTime createdTime, LocalDateTime lastChatTime) {
            this.id = id;
            this.name = name;
            this.chatRoomType = chatRoomType;
            this.participantList = participantList;
            this.createdTime = createdTime != null ? createdTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.lastChatTime = lastChatTime != null ? lastChatTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
