package com.CloseConnect.closeconnect.dto.post;

import com.CloseConnect.closeconnect.dto.comment.CommentDto;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.post.Post;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PostDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank
        @NotNull
        private String title;
        @NotBlank
        @NotNull
        private String content;

        public Post toEntity(Member author) {
            return Post.builder()
                    .author(author)
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    public record Response(
            Long id,
            String title,
            String content,
            String email,
            String authorName,
            String createdTime,
            String updatedTime,
            List<CommentDto.Response> comments) {

        public Response(Post post) {
            this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getEmail(),
                post.getAuthor().getName(),
                post.getCreatedTime() != null ? post.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                post.getUpdatedTime() != null ? post.getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                post.getComments().stream().map(CommentDto.Response::new).collect(Collectors.toList())
            );
        }
    }

    public record ResponseList(
            Long id,
            String title,
            String content,
            String authorName,
            String updatedTime
    ) {

        @QueryProjection
        public ResponseList(Long id, String title, String content, String authorName, String updatedTime) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.authorName = authorName;
            this.updatedTime = updatedTime;
        }
    }


}
