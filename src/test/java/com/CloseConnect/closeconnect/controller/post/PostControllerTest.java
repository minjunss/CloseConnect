package com.CloseConnect.closeconnect.controller.post;

import com.CloseConnect.closeconnect.WithMockCustomUser;
import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.dto.post.PostSearchCondition;
import com.CloseConnect.closeconnect.service.post.PostService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest {
    private final String BASE_POST_URI = "/api/posts";
    @MockBean
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockCustomUser
    void createPost() throws Exception {
        PostDto.Request request = new PostDto.Request("제목", "내용");
        PostDto.Response expectedResponse = new PostDto.Response(1L, "제목", "내용", "email", "이름",
                "2024-03-29 12:00:00", "2024-03-29 12:00:00", null);

        when(postService.save(any(PostDto.Request.class), any(String.class))).thenReturn(expectedResponse);


        mockMvc.perform(post(BASE_POST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.id()))
                .andExpect(jsonPath("$.title").value(expectedResponse.title()))
                .andExpect(jsonPath("$.content").value(expectedResponse.content()));
    }

    @Test
    @WithMockCustomUser
    void updatePost() throws Exception {
        Long postId = 1L;
        PostDto.Request request = new PostDto.Request(null, "내용");

        doNothing().when(postService).update(any(Long.class), any(PostDto.Request.class));

        mockMvc.perform(patch(BASE_POST_URI + "/update/{id}", postId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void deletePost() throws Exception {
        Long postId = 1L;

        doNothing().when(postService).delete(any(Long.class));

        mockMvc.perform(patch(BASE_POST_URI + "/delete/{id}", postId)
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void readPost() throws Exception {
        Long postId = 1L;
        PostDto.Response expectedResponse = new PostDto.Response(1L, "제목", "내용", "email", "작성자",
                "2024-03-29 12:00:00", "2024-03-29 12:00:00", null);

        when(postService.getPost(any(Long.class))).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_POST_URI + "/{id}", postId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.id()))
                .andExpect(jsonPath("$.title").value(expectedResponse.title()))
                .andExpect(jsonPath("$.content").value(expectedResponse.content()))
                .andExpect(jsonPath("$.createdTime").value(expectedResponse.createdTime()));
    }

    @Test
    @WithMockCustomUser
    void readPostList() throws Exception {
        List<PostDto.ResponseList> contentList = new ArrayList<>();
        contentList.add(new PostDto.ResponseList(1L, "title", "content", "authorName", "updatedTime"));
        contentList.add(new PostDto.ResponseList(2L, "title2", "content2", "authorName", "updatedTime"));
        contentList.add(new PostDto.ResponseList(3L, "title3", "content3", "authorName3", "updatedTime"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostDto.ResponseList> mockPage = new PageImpl<>(contentList, pageable, contentList.size());

        when(postService.getPostList(any(PostSearchCondition.class), any(Pageable.class))).thenReturn(mockPage);

        PostSearchCondition postSearchCondition = new PostSearchCondition();

        mockMvc.perform(get(BASE_POST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(postSearchCondition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.[0].title").value(contentList.get(0).title()))
                .andExpect(jsonPath("$.content.[1].title").value(contentList.get(1).title()))
                .andExpect(jsonPath("$.content.[2].title").value(contentList.get(2).title()));
    }
}