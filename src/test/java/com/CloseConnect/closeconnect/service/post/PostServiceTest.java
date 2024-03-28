package com.CloseConnect.closeconnect.service.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PostService postService;

    @Test
    public void testSavePost() {
        // Given
        String email = "test@test.com";
        Member author = Member.builder()
                .name("테스트 사용자")
                .email(email)
                .build();
        Post expectedPost = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .author(author)
                .build();
        PostDto.Request request = new PostDto.Request("테스트 제목", "테스트 내용");

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(postRepository.save(any(Post.class))).thenReturn(expectedPost);

        // When
        PostDto.Response savedPost = postService.save(request, email);

        // Then
        assertThat(savedPost.title()).isEqualTo(expectedPost.getTitle());
        assertThat(savedPost.content()).isEqualTo(expectedPost.getContent());
        assertThat(savedPost.authorName()).isEqualTo(author.getName());
    }

    @Test
    public void testFindPostById() {
        // Given
        Long postId = 1L;
        Post post = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .author(Member.builder().name("테스트 작성자").build())
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When
        PostDto.Response foundPost = postService.getPost(postId);

        // Then
        assertThat(foundPost.id()).isEqualTo(post.getId());
        assertThat(foundPost.title()).isEqualTo(post.getTitle());
        assertThat(foundPost.content()).isEqualTo(post.getContent());
        assertThat(foundPost.authorName()).isEqualTo(post.getAuthor().getName());
    }
}