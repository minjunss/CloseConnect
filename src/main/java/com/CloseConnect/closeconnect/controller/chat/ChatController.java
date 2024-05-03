package com.CloseConnect.closeconnect.controller.chat;

import com.CloseConnect.closeconnect.dto.chat.ChatDto;
import com.CloseConnect.closeconnect.service.chat.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @SendTo("/sub/chat")
    public void broadcasting(ChatDto.MessageRequest request) {
        log.info("request : {}}", request);

        //서비스로직
        chatService.saveChat(request);
    }

    @PostMapping("/createRoom")
    @Operation(summary = "채팅방 생성 API", description = "채팅방 생성")
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 생성 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatDto.RoomResponse.class)
            )
    )
    public ResponseEntity<?> createRoom(@RequestHeader("Authorization") String token,
                                        @RequestBody ChatDto.RoomRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.createChatRoom(request, userDetails.getUsername()));
    }

    @PostMapping("/participate/{roomId}")
    @Operation(summary = "채팅방 참가 API", description = "채팅방 참가자에 사용자 추가")
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 참가 성공"
    )
    public ResponseEntity<?> participateRoom(@RequestHeader("Authorization") String token,
                                             @PathVariable String roomId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        chatService.participantRoom(roomId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/outRoom/{roomId}")
    @Operation(summary = "채팅방 나가기 API", description = "채팅방 나가기")
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 나가기 성공"
    )
    public ResponseEntity<?> outRoom(@RequestHeader("Authorization") String token,
                                     @PathVariable String roomId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        chatService.outRoom(roomId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/myChatRooms")
    @Operation(summary = "내 채팅방 리스트 조회 API", description = "내가 참가중인 채팅방들 조회")
    @ApiResponse(
            responseCode = "200",
            description = "내 채팅방 리스트 조회 성공",
            content =
            @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChatDto.RoomResponse.class))
            )
    )
    public ResponseEntity<?> myChatRoomList(@RequestHeader("Authorization") String token,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.myChatRoomList(userDetails.getUsername()));
    }

    @GetMapping("/chatRoomList")
    @Operation(summary = "오픈 채팅방 조회 API", description = "오픈 채팅방들 조회")
    @ApiResponse(
            responseCode = "200",
            description = "오픈 채팅방 조회 성공",
            content =
            @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChatDto.RoomResponse.class))
            )
    )
    public ResponseEntity<?> openChatRoomList(@RequestHeader("Authorization") String token,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.openChatRoomList());
    }

    @GetMapping("/chatMessageList/{chatRoomId}")
    @Operation(summary = "특정 채팅방 메시지 조회 API", description = "특정 채팅방 메시지들 조회")
    @ApiResponse(
            responseCode = "200",
            description = "채팅방 메시지 조회 성공",
            content =
            @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChatDto.MessageResponse.class))
            )
    )
    public ResponseEntity<?> chatMessageList(@RequestHeader("Authorization") String token,
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatService.chatMessageList(chatRoomId));
    }
}
