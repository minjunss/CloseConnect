package com.CloseConnect.closeconnect.entity.chat;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom {

    @Id
    private String id;
    private String name;
    private List<Participant> participantList;
    private LocalDateTime createdTime;
    private LocalDateTime lastChatTime;

    @Builder
    public ChatRoom(String name, List<Participant> participantList, LocalDateTime createdTime, LocalDateTime lastChatTime) {
        this.name = name;
        this.participantList = participantList;
        this.createdTime = createdTime;
        this.lastChatTime = lastChatTime;
    }

    public void addParticipant(Participant participant) {
        this.participantList.add(participant);
    }

    public void setLastChatTime(LocalDateTime chatTime) {
        this.lastChatTime = chatTime;
    }
}
