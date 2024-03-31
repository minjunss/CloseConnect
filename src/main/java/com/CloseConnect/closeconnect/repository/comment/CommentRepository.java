package com.CloseConnect.closeconnect.repository.comment;

import com.CloseConnect.closeconnect.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
