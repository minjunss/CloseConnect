package com.CloseConnect.closeconnect.repository.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.dto.post.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostDto.ResponseList> getList(PostSearchCondition postSearch, Pageable pageable);
}
