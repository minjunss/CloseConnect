package com.CloseConnect.closeconnect.repository.post;

import com.CloseConnect.closeconnect.dto.post.PostDto;
import com.CloseConnect.closeconnect.dto.post.PostSearchCondition;
import com.CloseConnect.closeconnect.dto.post.QPostDto_ResponseList;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.CloseConnect.closeconnect.entity.post.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<PostDto.ResponseList> getList(PostSearchCondition postSearchCondition, Pageable pageable) {
        BooleanBuilder whereBuilder = new BooleanBuilder();
        if (postSearchCondition.getTitle() != null) {
            whereBuilder.and(post.title.contains(postSearchCondition.getTitle()));
        }
        if (postSearchCondition.getContent() != null) {
            whereBuilder.and(post.content.contains(postSearchCondition.getContent()));
        }
        if (postSearchCondition.getAuthor() != null) {
            whereBuilder.and(post.author.name.contains(postSearchCondition.getAuthor()));
        }

        List<PostDto.ResponseList> content = jpaQueryFactory
                .select(new QPostDto_ResponseList(
                        post.id,
                        post.title,
                        post.content,
                        post.author.name.as("authorName"),
                        post.updatedTime.stringValue()
                ))
                .from(post)
                .where(whereBuilder)
                .orderBy(post.updatedTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(post.count())
                .where(whereBuilder)
                .from(post);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
