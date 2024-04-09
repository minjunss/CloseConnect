package com.CloseConnect.closeconnect.global.config.batch;

import com.CloseConnect.closeconnect.entity.chat.ChatRoom;
import com.CloseConnect.closeconnect.repository.chat.ChatRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ChatRoomReader implements ItemReader<String> {
    private final ChatRoomRepository chatRoomRepository;
    private List<String> chatRoomIds;
    private int nextIndex;

    public ChatRoomReader(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.nextIndex = 0;
    }

    @Override
    public String read() {
        if (chatRoomIds == null) {
            chatRoomIds = chatRoomRepository.findByIsDeletedTrue().stream().map(ChatRoom::getId).collect(Collectors.toList());
        }

        String nextChatRoomId = null;

        if (nextIndex < chatRoomIds.size()) {
            nextChatRoomId = chatRoomIds.get(nextIndex);
            nextIndex++;
        }
        return nextChatRoomId;
    }
}
