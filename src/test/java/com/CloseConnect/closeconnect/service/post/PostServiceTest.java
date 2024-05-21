package com.CloseConnect.closeconnect.service.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.dto.post.PostSearchCondition;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("mysql")
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostService postService;
    private final String email = "test@test.com";
    private final Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
        memberRepository.deleteAll();
        memberRepository.save(member);
    }

    @Test
    public void testSavePost() {
        // Given
        PostDto.Request request = PostDto.Request.builder()
                .title("title")
                .content("content")
                .build();

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
        //given
        List<Post> request = IntStream.range(0, 20)
                .mapToObj(i -> Post.builder()
                        .author(member)
                        .title("title" + i)
                        .content("content" + i)
                        .build())
                .toList();

        postRepository.saveAll(request);

        PostSearchCondition postSearchCondition = new PostSearchCondition();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        //when
        Page<PostDto.ResponseList> response = postRepository.getList(postSearchCondition, pageable);

        //then
        assertThat(response.getContent().size()).isEqualTo(10);
        assertThat(response.getContent().get(0).title()).isEqualTo("title19");
        assertThat(response.getContent().get(2).title()).isEqualTo("title17");


    }

    @Test
    void testGetPostsByEmail() {
        //given
        Member member2 = new Member("name2", "test2@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        memberRepository.save(member2);

        List<Post> request = IntStream.range(0, 10)
                .mapToObj(i -> Post.builder()
                        .author(member)
                        .title("title" + i)
                        .content("content" + i)
                        .build())
                .toList();
        postRepository.saveAll(request);

        List<Post> request2 = IntStream.range(0, 10)
                .mapToObj(i -> Post.builder()
                        .author(member2)
                        .title("title" + i)
                        .content("content" + i)
                        .build())
                .toList();
        postRepository.saveAll(request2);

        //when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<PostDto.ResponseList> response = postService.getPostsByEmail(member.getEmail(), pageable);

        //then
        assertThat(response.getContent().size()).isEqualTo(10);
    }

    @Test
    void testUpdatePost() {
        //given
        Post post = Post.builder()
                .author(member)
                .title("title")
                .content("content")
                .build();
        Post savedPost = postRepository.save(post);

        PostDto.Request request = PostDto.Request.builder()
                .title(null)
                .content("updateTest")
                .build();

        //when
        postService.update(savedPost.getId(), request);
        Optional<Post> updatedPost = postRepository.findById(savedPost.getId());

        //then
        assertThat(updatedPost.get().getContent()).isEqualTo("updateTest");
    }

    @Test
    void testDelete() {
        //given
        Post post = Post.builder()
                .author(member)
                .title("title")
                .content("content")
                .build();
        Post savedPost = postRepository.save(post);

        //when
        postService.delete(savedPost.getId());
        Optional<Post> deletedPost = postRepository.findById(savedPost.getId());

        //then
        assertThat(deletedPost.get().isDeleted()).isTrue();
    }
}