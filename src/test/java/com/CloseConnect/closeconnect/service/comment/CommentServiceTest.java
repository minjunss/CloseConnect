package com.CloseConnect.closeconnect.service.comment;

import com.CloseConnect.closeconnect.dto.comment.CommentDto;
import com.CloseConnect.closeconnect.entity.comment.Comment;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.repository.comment.CommentRepository;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.repository.post.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("mysql")
class CommentServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @BeforeEach
    void clean() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void testSave() {
        //given
        Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Member author = memberRepository.save(member);
        Post post = new Post(author, "title", "content");
        Post savedPost = postRepository.save(post);

        CommentDto.Request request = CommentDto.Request.builder()
                        .postId(savedPost.getId())
                        .content("saveContent")
                        .build();
        //when
        CommentDto.Response response = commentService.save(request, author.getEmail());

        //then
        assertThat(response.getContent()).isEqualTo("saveContent");

    }

    @Test
    void testUpdate() {
        //given
        Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Member author = memberRepository.save(member);
        Post post = new Post(author, "title", "content");
        Post savedPost = postRepository.save(post);

        Comment comment = new Comment("comment",author, savedPost);
        Comment savedComment = commentRepository.save(comment);

        CommentDto.Request request = CommentDto.Request.builder()
                .postId(savedPost.getId())
                .content("updateComment")
                .build();
        //when
        commentService.update(savedComment.getId(), request);
        Optional<Comment> updatedComment = commentRepository.findById(savedComment.getId());

        //then
        assertThat(updatedComment.get().getContent()).isEqualTo("updateComment");
    }

    @Test
    void testDelete() {
        //given
        Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Member author = memberRepository.save(member);
        Post post = new Post(author, "title", "content");
        Post savedPost = postRepository.save(post);

        Comment comment = new Comment("comment",author, savedPost);
        Comment savedComment = commentRepository.save(comment);

        //when
        commentService.delete(savedComment.getId());
        Optional<Comment> deletedComment = commentRepository.findById(savedComment.getId());

        //then
        assertThat(deletedComment.get().isDeleted()).isTrue();
    }

    @Test
    void testGetCommentsByPostId() {
        //given
        Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Member author = memberRepository.save(member);
        Post post = new Post(author, "title", "content");
        Post post2 = new Post(author, "title2", "content2");
        Post savedPost = postRepository.save(post);
        Post savedPost2 = postRepository.save(post2);

        Comment comment = new Comment("comment",author, savedPost);
        Comment comment2 = new Comment("comment2",author, savedPost2);
        Comment comment3 = new Comment("comment3",author, savedPost);
        Comment comment4 = new Comment("comment4",author, savedPost);

        commentRepository.saveAll(List.of(comment, comment2, comment3, comment4));

        //when
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentDto.Response> response = commentService.getCommentsByPostId(savedPost.getId(), pageable);

        //then
        assertThat(response.getContent().size()).isEqualTo(3);
    }

    @Test
    void testGetCommentsByEmail() {
        //given
        Member member = new Member("name", "test@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Member member2 = new Member("name2", "test2@test.com", "abcd1234", AuthProvider.GOOGLE, Role.USER, "Y");
        Member author = memberRepository.save(member);
        Member author2 = memberRepository.save(member2);
        Post post = new Post(author, "title", "content");
        Post savedPost = postRepository.save(post);

        Comment comment = new Comment("comment",author, savedPost);
        Comment comment2 = new Comment("comment2",author, savedPost);
        Comment comment3 = new Comment("comment3",author2, savedPost);
        Comment comment4 = new Comment("comment4",author2, savedPost);
        Comment comment5 = new Comment("comment5",author2, savedPost);

        commentRepository.saveAll(List.of(comment, comment2, comment3, comment4, comment5));

        //when
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentDto.Response> response = commentService.getCommentsByEmail("test2@test.com", pageable);

        //then
        assertThat(response.getContent().size()).isEqualTo(3);
        assertThat(response.getContent().get(0).getAuthorName()).isEqualTo("name2");
    }
}














