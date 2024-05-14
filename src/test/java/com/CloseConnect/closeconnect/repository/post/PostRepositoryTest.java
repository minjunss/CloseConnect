package com.CloseConnect.closeconnect.repository.post;

import com.CloseConnect.closeconnect.config.QueryDslTestConfig;
import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.dto.post.PostSearchCondition;
import com.CloseConnect.closeconnect.entity.member.AuthProvider;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.member.Role;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.global.config.QueryDslConfig;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(QueryDslTestConfig.class)
@ActiveProfiles("mysql")
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    public void getListTest() throws Exception {
        //given
        Member member = new Member("testName", "test@test.com", "testOAuth2Id", AuthProvider.GOOGLE, Role.USER, "Y");
        memberRepository.save(member);
        PostSearchCondition searchCondition = new PostSearchCondition();
        searchCondition.setTitle("testTitle");
        Pageable pageable = PageRequest.of(0, 10);

        Post post = new Post(member, "testTitle", "testContent");
        Post post2 = new Post(member, "testTitle2", "testContent2");
        Post post3 = new Post(member, "gadf34", "testContent3");
        postRepository.saveAll(List.of(post, post2, post3));

        //when
        Page<PostDto.ResponseList> response = postRepository.getList(searchCondition, pageable);

        //then
        assertThat(response.getContent().size()).isEqualTo(2);
    }
}