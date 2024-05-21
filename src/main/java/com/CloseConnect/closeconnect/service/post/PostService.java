package com.CloseConnect.closeconnect.service.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.dto.post.PostSearchCondition;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.global.exception.BusinessException;
import com.CloseConnect.closeconnect.global.exception.ExceptionCode;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostDto.Response save(PostDto.Request request, String email) {
        Member author = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_MEMBER, email));
        Post post = request.toEntity(author);
        postRepository.save(post);

        return new PostDto.Response(post);
    }

    public void update(Long postId, PostDto.Request request) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_POST, String.valueOf(postId)));
        post.update(request.getContent());
    }

    public void delete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_POST, String.valueOf(postId)));
        post.delete();
    }

    @Transactional(readOnly = true)
    public PostDto.Response getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_POST, String.valueOf(id)));

        return new PostDto.Response(post);
    }

    @Transactional(readOnly = true)
    public Page<PostDto.ResponseList> getPostList(PostSearchCondition postSearchCondition, Pageable pageable) {
        Page<PostDto.ResponseList> postList = postRepository.getList(postSearchCondition, pageable);

        return postList;
    }

    @Transactional(readOnly = true)
    public Page<PostDto.ResponseList> getPostsByEmail(String email, Pageable pageable) {
        Page<PostDto.ResponseList> response = postRepository.findByEmail(email, pageable);

        return response;
    }
}
