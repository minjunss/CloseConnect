package com.CloseConnect.closeconnect.entity.comment;

import com.CloseConnect.closeconnect.entity.BaseTime;
import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    private boolean isDeleted;
    private LocalDateTime deletedTime;

    @Builder
    public Comment(String content, Member author, Post post) {
        this.content = content;
        this.author = author;
        this.post = post;
    }

    public void update(String content) {
        this.content = content;
    }
    public void delete() {
        this.isDeleted = true;
        this.deletedTime = LocalDateTime.now();
    }
}
