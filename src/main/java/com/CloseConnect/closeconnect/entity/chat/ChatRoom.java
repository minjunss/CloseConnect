package com.CloseConnect.closeconnect.entity.chat;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom {

    @Id
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;
    private List<Participant> participantList = new ArrayList<>();
    private LocalDateTime createdTime;
    private LocalDateTime lastChatTime;
    private boolean isDeleted;
    private LocalDateTime deletedTime;

    @Builder
    public ChatRoom(String name,ChatRoomType chatRoomType, List<Participant> participantList, LocalDateTime createdTime, LocalDateTime lastChatTime) {
        this.name = name;
        this.chatRoomType = chatRoomType;
        this.participantList = participantList != null ? participantList : new ArrayList<>();
        this.createdTime = createdTime;
        this.lastChatTime = lastChatTime;
    }

    public void addParticipant(Participant participant) {
        this.participantList.add(participant);
    }

    public void setLastChatTime(LocalDateTime chatTime) {
        this.lastChatTime = chatTime;
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedTime = LocalDateTime.now();
    }
}
