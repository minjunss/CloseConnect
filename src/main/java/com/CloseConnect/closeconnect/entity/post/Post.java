package com.CloseConnect.closeconnect.entity.post;

import com.CloseConnect.closeconnect.entity.BaseTime;
import com.CloseConnect.closeconnect.entity.comment.Comment;
import com.CloseConnect.closeconnect.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;
    private String title;
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(Member author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }
}
