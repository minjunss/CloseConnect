package com.CloseConnect.closeconnect.global.config.batch;

import com.CloseConnect.closeconnect.entity.chat.ChatMessage;
import com.CloseConnect.closeconnect.repository.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@RequiredArgsConstructor
public class ChatMessageItemWriter implements ItemWriter<Object> {
    private final ChatMessageRepository chatMessageRepository;


    @Override
    public void write(Chunk<?> chunk) throws Exception {
        List<? extends List<ChatMessage>> items = (List<? extends List<ChatMessage>>) chunk.getItems();

        for (List<ChatMessage> chatMessageList : items) {
            chatMessageRepository.saveAll(chatMessageList);
        }
    }
}
