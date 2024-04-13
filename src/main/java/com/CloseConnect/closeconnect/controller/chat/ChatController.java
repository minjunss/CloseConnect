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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/sub/chat")
    public void broadcasting(ChatDto.MessageRequest request) {
        log.info("request : {}}", request);

        //서비스로직
        chatService.saveChat(request);
    }

    //TODO: swagger 설정
    @PostMapping("/createRoom")
    public ResponseEntity<?> createRoom(@RequestBody ChatDto.RoomRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.createChatRoom(request, userDetails.getUsername()));
    }

    //TODO: swagger 설정
    @PostMapping("/participate/{roomId}")
    public ResponseEntity<?> participateRoom(@PathVariable String roomId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        chatService.participantRoom(roomId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    //TODO: swagger설정
    @PostMapping("/outRoom/{roomId}")
    public ResponseEntity<?> outRoom(@PathVariable String roomId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        chatService.outRoom(roomId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    //TODO: swagger설정
    @GetMapping("/myChatRooms")
    public ResponseEntity<?> myChatRoomList(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.myChatRoomList(userDetails.getUsername()));
    }

    @GetMapping("/chatRoomList")
    public ResponseEntity<?> chatRoomList(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.chatRoomList());
    }

    @GetMapping("/chatMessageList/{chatRoomId}")
    public ResponseEntity<?> chatMessageList(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatService.chatMessageList(chatRoomId));
    }
}
