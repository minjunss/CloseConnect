package com.CloseConnect.closeconnect.service.comment;

import com.CloseConnect.closeconnect.dto.comment.CommentDto;
import com.CloseConnect.closeconnect.entity.comment.Comment;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.CloseConnect.closeconnect.global.exception.BusinessException;
import com.CloseConnect.closeconnect.global.exception.ExceptionCode;
import com.CloseConnect.closeconnect.repository.comment.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentDto.Response save(CommentDto.Request request, String email) {
        Member author = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_MEMBER, email));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_POST, String.valueOf(request.getPostId())));
        Comment comment = request.toEntity(author, post);

        commentRepository.save(comment);

        return new CommentDto.Response(comment);
    }
    @Transactional
    public void update(Long commentId, CommentDto.Request request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_COMMENT, String.valueOf(commentId)));
        comment.update(request.getContent());
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.NOT_EXIST_COMMENT, String.valueOf(commentId)));
        comment.delete();
    }

    public Page<CommentDto.Response> getCommentsByPostId(Long postId, Pageable pageable) {
        Page<CommentDto.Response> response = commentRepository.findByPostId(postId, pageable);
        return response;
    }

    public Page<CommentDto.Response> getCommentsByEmail(String email, Pageable pageable) {
        Page<CommentDto.Response> response = commentRepository.findByEmail(email, pageable);
        return response;
    }
}
