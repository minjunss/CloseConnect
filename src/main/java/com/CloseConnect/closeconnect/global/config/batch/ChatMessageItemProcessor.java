package com.CloseConnect.closeconnect.global.config.batch;

import com.CloseConnect.closeconnect.entity.chat.ChatMessage;
import com.CloseConnect.closeconnect.repository.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
public class ChatMessageItemProcessor implements ItemProcessor<Object, List<ChatMessage>> {

    private final ChatMessageRepository chatMessageRepository;


    @Override
    public List<ChatMessage> process(Object roomId) throws Exception {
        List<ChatMessage> chatMessageList = chatMessageRepository.findByRoomId((String)roomId);

        for (ChatMessage chatMessage : chatMessageList) {
            chatMessage.delete();
        }
        return chatMessageList;
    }
}
