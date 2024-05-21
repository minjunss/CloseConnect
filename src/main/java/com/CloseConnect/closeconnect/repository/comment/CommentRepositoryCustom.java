package com.CloseConnect.closeconnect.repository.comment;

import com.CloseConnect.closeconnect.dto.comment.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<CommentDto.Response> findByPostId(Long postId, Pageable pageable);
    Page<CommentDto.Response> findByEmail(String email, Pageable pageable);
}
