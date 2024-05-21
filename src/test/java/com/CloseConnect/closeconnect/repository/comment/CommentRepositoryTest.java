package com.CloseConnect.closeconnect.repository.comment;

import com.CloseConnect.closeconnect.config.QueryDslTestConfig;
import com.CloseConnect.closeconnect.dto.comment.CommentDto;
import com.CloseConnect.closeconnect.entity.comment.Comment;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(QueryDslTestConfig.class)
@ActiveProfiles("mysql")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeAll
    void setup() {
        postRepository.deleteAll();
        memberRepository.deleteAll();
        Member member = new Member("testName", "test@test.com", "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        memberRepository.save(member);
        Post post = Post.builder()
                .author(member)
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);

    }

    @BeforeEach
    void clean() {
        commentRepository.deleteAll();
    }

    @Test
    void testFindByPostId() {
        //given
        Optional<Post> post = postRepository.findById(1L);
        Member member = new Member("testName2", "test2@test.com", "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        Member author = memberRepository.save(member);

        Post post2 = Post.builder()
                .author(member)
                .title("title")
                .content("content")
                .build();
        Post otherPost = postRepository.save(post2);

        Comment comment = Comment.builder()
                .author(author)
                .post(post.get())
                .content("content")
                .build();
        Comment comment2 = Comment.builder()
                .author(author)
                .post(post.get())
                .content("content2")
                .build();
        Comment comment3 = Comment.builder()
                .author(author)
                .post(otherPost)
                .content("content3")
                .build();

        commentRepository.saveAll(List.of(comment, comment2, comment3));

        //when
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentDto.Response> response = commentRepository.findByPostId(post.get().getId(), pageable);

        //then
        assertThat(response.getContent().size()).isEqualTo(2);
    }

    @Test
    void testFindByEmail() {
        //given
        Member member2 = new Member("testName2", "test2@test.com", "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        Member member3 = new Member("testName3", "test3@test.com", "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        Member author = memberRepository.save(member2);
        Member author2 = memberRepository.save(member3);

        Optional<Post> post = postRepository.findById(1L);

        Comment comment = Comment.builder()
                .author(author)
                .post(post.get())
                .content("content")
                .build();
        Comment comment2 = Comment.builder()
                .author(author)
                .post(post.get())
                .content("content2")
                .build();
        Comment comment3 = Comment.builder()
                .author(author2)
                .post(post.get())
                .content("content3")
                .build();
        Comment comment4 = Comment.builder()
                .author(author2)
                .post(post.get())
                .content("content3")
                .build();
        Comment comment5 = Comment.builder()
                .author(author2)
                .post(post.get())
                .content("content3")
                .build();

        commentRepository.saveAll(List.of(comment, comment2, comment3, comment4, comment5));

        //when
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentDto.Response> response = commentRepository.findByEmail(author2.getEmail(), pageable);

        //then
        assertThat(response.getContent().size()).isEqualTo(3);
    }
}













