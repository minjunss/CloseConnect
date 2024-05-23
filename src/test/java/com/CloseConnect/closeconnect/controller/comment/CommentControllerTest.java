package com.CloseConnect.closeconnect.controller.comment;

import com.CloseConnect.closeconnect.WithMockCustomUser;
import com.CloseConnect.closeconnect.dto.comment.CommentDto;
import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.entity.comment.Comment;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.service.comment.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CommentControllerTest {
    private final String BASE_COMMENT_URI = "/api/comment";
    @MockBean
    private CommentService commentService;
    @InjectMocks
    private CommentController commentController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockCustomUser
    void createComment() throws Exception {
        Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Post post = new Post(member, "title", "content");
        CommentDto.Request request = new CommentDto.Request(1L, "content");
        CommentDto.Response expectedResponse = new CommentDto.Response(new Comment("content", member, post));

        when(commentService.save(any(CommentDto.Request.class), any(String.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_COMMENT_URI + "/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("Authorization", "Bearer Token")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.authorName").value(expectedResponse.getAuthorName()))
                .andExpect(jsonPath("$.postId").value(expectedResponse.getPostId()));
    }

    @Test
    @WithMockCustomUser
    void updateComment() throws Exception {
        Long commentId = 1L;
        CommentDto.Request request = new CommentDto.Request(1L, "updatedContent");

        doNothing().when(commentService).update(any(Long.class), any(CommentDto.Request.class));

        mockMvc.perform(patch(BASE_COMMENT_URI + "/update/{id}", commentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .header("Authorization", "Bearer Token")
                    .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void deleteComment() throws Exception {
        Long commentId = 1L;

        doNothing().when(commentService).delete(any(Long.class));

        mockMvc.perform(patch(BASE_COMMENT_URI + "/delete/{id}", commentId)
                        .header("Authorization", "Bearer Token")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void readCommentsByPostId() throws Exception {
        Long postId = 1L;
        List<CommentDto.Response> response = new ArrayList<>();
        Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Post post = new Post(member, "title", "content");
        response.add(new CommentDto.Response(new Comment("content1", member, post)));
        response.add(new CommentDto.Response(new Comment("content2", member, post)));
        response.add(new CommentDto.Response(new Comment("content3", member, post)));

        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentDto.Response> mockPage = new PageImpl<>(response, pageable, response.size());

        when(commentService.getCommentsByPostId(any(Long.class), any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get(BASE_COMMENT_URI + "/listByPost/{postId}", postId)
                    .header("Authorization", "Bearer Token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.[1].content").value("content2"))
                .andExpect(jsonPath("$.totalElements").value(3));

    }

    @Test
    @WithMockCustomUser
    void readCommentsByEmail() throws Exception {
        Long postId = 1L;
        List<CommentDto.Response> response = new ArrayList<>();
        Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Post post = new Post(member, "title", "content");
        response.add(new CommentDto.Response(new Comment("content1", member, post)));
        response.add(new CommentDto.Response(new Comment("content2", member, post)));
        response.add(new CommentDto.Response(new Comment("content3", member, post)));
        response.add(new CommentDto.Response(new Comment("content4", member, post)));

        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentDto.Response> mockPage = new PageImpl<>(response, pageable, response.size());

        when(commentService.getCommentsByEmail(any(String.class), any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get(BASE_COMMENT_URI + "/myList", postId)
                        .header("Authorization", "Bearer Token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.[2].content").value("content3"))
                .andExpect(jsonPath("$.totalElements").value(4));

    }
}