package com.CloseConnect.closeconnect.dto.comment;

import com.CloseConnect.closeconnect.entity.comment.Comment;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long postId;
        private String content;

        public Comment toEntity(Member member, Post post) {
            return Comment.builder()
                    .author(member)
                    .post(post)
                    .content(content)
                    .build();
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String content;
        private final String authorName;
        private final Long authorId;
        private final Long postId;
        private final String createdTime;
        private final String updatedTime;

        @QueryProjection
        public Response(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.authorName = comment.getAuthor().getName();
            this.authorId = comment.getAuthor().getId();
            this.postId = comment.getPost().getId();
            this.createdTime = comment.getCreatedTime() != null ? comment.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.updatedTime = comment.getUpdatedTime() != null ? comment.getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }


}
