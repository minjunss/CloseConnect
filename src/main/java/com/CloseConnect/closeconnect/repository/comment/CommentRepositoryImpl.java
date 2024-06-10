package com.CloseConnect.closeconnect.repository.comment;

import com.CloseConnect.closeconnect.dto.comment.CommentDto;
import com.CloseConnect.closeconnect.dto.comment.QCommentDto_Response;
import com.CloseConnect.closeconnect.entity.comment.QComment;
import com.CloseConnect.closeconnect.entity.member.QMember;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.CloseConnect.closeconnect.entity.comment.QComment.*;
import static com.CloseConnect.closeconnect.entity.member.QMember.*;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<CommentDto.Response> findByPostId(Long postId, Pageable pageable) {

        List<CommentDto.Response> content = jpaQueryFactory
                .select(new QCommentDto_Response(comment))
                .from(comment)
                .where(comment.post.id.eq(postId)
                        .and(comment.isDeleted.eq(false)))
                .orderBy(comment.updatedTime.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(postId)
                        .and(comment.isDeleted.eq(false)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<CommentDto.Response> findByEmail(String email, Pageable pageable) {

        List<CommentDto.Response> content = jpaQueryFactory
                .select(new QCommentDto_Response(comment))
                .from(comment)
                //.join(comment.author, member)
                .where(comment.author.email.eq(email)
                        .and(comment.isDeleted.eq(false)))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                //.join(comment.author, member)
                .where(comment.author.email.eq(email)
                        .and(comment.isDeleted.eq(false)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
