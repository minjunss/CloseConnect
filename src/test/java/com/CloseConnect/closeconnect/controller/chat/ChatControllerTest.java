package com.CloseConnect.closeconnect.controller.chat;

import com.CloseConnect.closeconnect.WithMockCustomUser;
import com.CloseConnect.closeconnect.dto.chat.ChatDto;
import com.CloseConnect.closeconnect.entity.chat.ChatRoomType;
import com.CloseConnect.closeconnect.service.chat.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ChatController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ChatControllerTest {

    private final String BASE_POST_URI = "/api/chat";

    @MockBean
    private ChatService chatService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockCustomUser
    public void createRoom() throws Exception {

        ChatDto.RoomRequest request = new ChatDto.RoomRequest("테스트방", ChatRoomType.PUBLIC);
        ChatDto.RoomResponse ExpectedResponse = ChatDto.RoomResponse.builder().id("1456").build();

        when(chatService.createChatRoom(any(ChatDto.RoomRequest.class), any(String.class))).thenReturn(ExpectedResponse);

        mockMvc.perform(post(BASE_POST_URI + "/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ExpectedResponse.getId()));
    }

    @Test
    @WithMockCustomUser
    public void participateRoom() throws Exception {
        String roomId = "test1234";

        doNothing().when(chatService).participantRoom(any(String.class), any(String.class));

        mockMvc.perform(post(BASE_POST_URI + "/participate/{roomId}", roomId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    public void outRoom() throws Exception {
        String roomId = "test1234";

        doNothing().when(chatService).outRoom(any(String.class), any(String.class));

        mockMvc.perform(post(BASE_POST_URI + "/outRoom/{roomId}", roomId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    public void myChatRoomList() throws Exception {
        List<ChatDto.RoomResponse> expectedResponse = new ArrayList<>();
        expectedResponse.add(new ChatDto.RoomResponse("test1", "테스트방1", ChatRoomType.PUBLIC, new ArrayList<>(), null, null));
        expectedResponse.add(new ChatDto.RoomResponse("test2", "테스트방2", ChatRoomType.PRIVATE, new ArrayList<>(), null, null));
        expectedResponse.add(new ChatDto.RoomResponse("test3", "테스트방3", ChatRoomType.PUBLIC, new ArrayList<>(), null, null));
        expectedResponse.add(new ChatDto.RoomResponse("test4", "테스트방4", ChatRoomType.PRIVATE, new ArrayList<>(), null, null));

        when(chatService.myChatRoomList(any(String.class))).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_POST_URI + "/myChatRooms")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath("$.[1].id").value(expectedResponse.get(1).getId()))
                .andExpect(jsonPath("$.[2].chatRoomType").value(expectedResponse.get(2).getChatRoomType().getKey()))
                .andExpect(jsonPath("$.[3].name").value(expectedResponse.get(3).getName()));
    }

    @Test
    @WithMockCustomUser
    public void chatRoomList() throws Exception {
        List<ChatDto.RoomResponse> expectedResponse = new ArrayList<>();
        expectedResponse.add(new ChatDto.RoomResponse("test1", "테스트방1", ChatRoomType.PUBLIC, new ArrayList<>(), null, null));
        expectedResponse.add(new ChatDto.RoomResponse("test2", "테스트방2", ChatRoomType.PRIVATE, new ArrayList<>(), null, null));
        expectedResponse.add(new ChatDto.RoomResponse("test3", "테스트방3", ChatRoomType.PUBLIC, new ArrayList<>(), null, null));
        expectedResponse.add(new ChatDto.RoomResponse("test4", "테스트방4", ChatRoomType.PRIVATE, new ArrayList<>(), null, null));

        when(chatService.openChatRoomList()).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_POST_URI + "/chatRoomList")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath("$.[1].id").value(expectedResponse.get(1).getId()))
                .andExpect(jsonPath("$.[2].chatRoomType").value(expectedResponse.get(2).getChatRoomType().getKey()))
                .andExpect(jsonPath("$.[3].name").value(expectedResponse.get(3).getName()));
    }

    @Test
    @WithMockCustomUser
    public void chatMessageList() throws Exception {
        String chatRoomId = "test1234";

        List<ChatDto.MessageResponse> expectedResponse = new ArrayList<>();
        expectedResponse.add(ChatDto.MessageResponse.builder().id("test1").message("message1").senderName("sender1").senderId("sId1").time(null).build());
        expectedResponse.add(ChatDto.MessageResponse.builder().id("test2").message("message2").senderName("sender2").senderId("sId2").time(null).build());
        expectedResponse.add(ChatDto.MessageResponse.builder().id("test3").message("message3").senderName("sender3").senderId("sId3").time(null).build());

        when(chatService.chatMessageList(any(String.class))).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_POST_URI + "/chatMessageList/{chatRoomId}", chatRoomId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath("$.[0].message").value(expectedResponse.get(0).getMessage()))
                .andExpect(jsonPath("$.[1].senderName").value(expectedResponse.get(1).getSenderName()))
                .andExpect(jsonPath("$.[2].time").value(expectedResponse.get(2).getTime()));
    }
}











