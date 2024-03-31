package com.CloseConnect.closeconnect.service.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.dto.post.PostSearchCondition;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostDto.Response save(PostDto.Request request, String email) {
        Member author = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 존재하지 않음. email: " + email));
        Post post = request.toEntity(author);
        postRepository.save(post);

        return new PostDto.Response(post);
    }

    public PostDto.Response getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않음. id: " + id));

        return new PostDto.Response(post);
    }

    public Page<PostDto.ResponseList> getPostList(PostSearchCondition postSearchCondition, Pageable pageable) {
        Page<PostDto.ResponseList> postList = postRepository.getList(postSearchCondition, pageable);

        return postList;
    }
}
