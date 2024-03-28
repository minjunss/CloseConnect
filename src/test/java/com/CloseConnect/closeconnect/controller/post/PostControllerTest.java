package com.CloseConnect.closeconnect.controller.post;

import com.CloseConnect.closeconnect.WithMockCustomUser;
import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.service.post.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        PostDto.Response expectedResponse = new PostDto.Response(1L, "제목", "내용", 1L, "이름",
                "2024-03-29 12:00:00", "2024-03-29 12:00:00", null);

        when(postService.save(any(PostDto.Request.class), any(String.class))).thenReturn(expectedResponse);


        mockMvc.perform(post(BASE_POST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.id()))
                .andExpect(jsonPath("$.title").value(expectedResponse.title()))
                .andExpect(jsonPath("$.content").value(expectedResponse.content()));
    }

    @Test
    @WithMockCustomUser
    void readPost() throws Exception {
        Long postId = 1L;
        PostDto.Response expectedResponse = new PostDto.Response(1L, "제목", "내용", 1L, "작성자",
                "2024-03-29 12:00:00", "2024-03-29 12:00:00", null);

        when(postService.getPost(postId)).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_POST_URI + "/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.id()))
                .andExpect(jsonPath("$.title").value(expectedResponse.title()))
                .andExpect(jsonPath("$.content").value(expectedResponse.content()))
                .andExpect(jsonPath("$.createdTime").value(expectedResponse.createdTime()));
    }
}