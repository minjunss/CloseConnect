package com.CloseConnect.closeconnect.repository.post;

import com.CloseConnect.closeconnect.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{
}
