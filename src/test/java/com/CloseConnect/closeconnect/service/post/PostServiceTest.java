package com.CloseConnect.closeconnect.service.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostService postService;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
        memberRepository.deleteAll();
        memberRepository.save(new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y"));
    }

    @Test
    public void testSavePost() {
        // Given
        PostDto.Request request = PostDto.Request.builder()
                .title("title")
                .content("content")
                .build();
        String email = "test@test.com";

        Optional<Member> findMember = memberRepository.findByEmail(email);

        // When
        PostDto.Response savedPost = postService.save(request, email);

        // Then
        assertThat(savedPost.title()).isEqualTo(request.getTitle());
        assertThat(savedPost.content()).isEqualTo(request.getContent());
        assertThat(savedPost.authorName()).isEqualTo(findMember.get().getName());
    }

    @Test
    public void testFindPostById() {
        // Given
        PostDto.Request request = PostDto.Request.builder()
                .title("title")
                .content("content")
                .build();
        String email = "test@test.com";

        PostDto.Response post = postService.save(request, email);

        // When
        PostDto.Response foundPost = postService.getPost(post.id());

        // Then
        assertThat(foundPost.id()).isEqualTo(post.id());
        assertThat(foundPost.title()).isEqualTo(request.getTitle());
        assertThat(foundPost.content()).isEqualTo(request.getContent());
        assertThat(foundPost.authorName()).isEqualTo(post.authorName());
    }

    @Test
    void testFindPostList() {

    }
}