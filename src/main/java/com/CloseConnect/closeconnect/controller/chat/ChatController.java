package com.CloseConnect.closeconnect.controller.chat;

import com.CloseConnect.closeconnect.dto.chat.ChatDto;
import com.CloseConnect.closeconnect.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/sub/chat/{id}")
    public void broadcasting(ChatDto.Message request) {
        log.info("request : {}}", request);

        //서비스로직
        chatService.saveChat(request);
    }

    //TODO: swagger 설정
    @PostMapping("/createRoom")
    public ResponseEntity<?> createRoom(@RequestBody ChatDto.Room request) {
        chatService.createChatRoom(request);
        return ResponseEntity.ok().build();
    }

    //TODO: swagger 설정
    @PostMapping("/participate/{roomId}")
    public ResponseEntity<?> participateRoom(@PathVariable String roomId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        chatService.participantRoom(roomId, username);

        return ResponseEntity.ok().build();
    }
}
