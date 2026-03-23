package com.cute.gawm.domain.like.repository;

import com.cute.gawm.common.util.QueryDslSupport;
import com.cute.gawm.domain.like.entity.Likes;
import com.cute.gawm.domain.lookbook.dto.TopLookBookIdsDto;
import com.querydsl.core.types.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static com.cute.gawm.domain.like.entity.QLikes.likes;
import static com.cute.gawm.domain.lookbook.entity.QLookbook.lookbook;

@Repository
public class LikeRepositoryCustomImpl extends QueryDslSupport implements LikeRepositoryCustom {
    @Autowired
    public LikeRepositoryCustomImpl(EntityManager entityManager) {
        super(Likes.class, entityManager);
    }

    @Override
    public List<TopLookBookIdsDto> findTopLookbook_v3(LocalDateTime startAt, LocalDateTime endAt) {
        return queryFactory.select(Projections.constructor(TopLookBookIdsDto.class,
                        likes.lookbook.lookbookId,
                        likes.count().as("likeCount")))
                .from(likes)
                .where(
                        likes.updatedAt.goe(startAt),
                        likes.updatedAt.lt(endAt)
                )
                .groupBy(likes.lookbook.lookbookId)
                .orderBy(likes.count().desc())
                .limit(15)
                .fetch();
    }
}
