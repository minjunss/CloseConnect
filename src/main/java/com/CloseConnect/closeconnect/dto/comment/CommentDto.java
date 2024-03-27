package com.CloseConnect.closeconnect.dto.comment;

import com.CloseConnect.closeconnect.entity.comment.Comment;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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

        public Response(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.authorName = comment.getAuthor().getName();
            this.authorId = comment.getAuthor().getId();
            this.postId = comment.getPost().getId();
        }
    }


}
