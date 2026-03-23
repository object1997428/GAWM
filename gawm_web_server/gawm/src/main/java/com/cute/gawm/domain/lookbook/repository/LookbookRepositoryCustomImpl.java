package com.cute.gawm.domain.lookbook.repository;

import com.cute.gawm.common.exception.TagNotExistException;
import com.cute.gawm.common.util.QueryDslSupport;
import com.cute.gawm.domain.clothes.entity.QClothes;
import com.cute.gawm.domain.clothes_lookbook.entity.QClothesLookbook;
import com.cute.gawm.domain.like.entity.QLikes;
import com.cute.gawm.domain.lookbook.dto.TopLookBookDto;
import com.cute.gawm.domain.lookbook.dto.response.LookBookTopResponse_v2;
import com.cute.gawm.domain.lookbook.entity.Lookbook;
import com.cute.gawm.domain.tag.entity.QTag;
import com.cute.gawm.domain.tag_lookbook.entity.QTagLookbook;
import com.cute.gawm.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cute.gawm.domain.like.entity.QLikes.likes;
import static com.cute.gawm.domain.lookbook.entity.QLookbook.lookbook;



@Repository
public class LookbookRepositoryCustomImpl extends QueryDslSupport implements LookbookRepositoryCustom{
    @Autowired
    public LookbookRepositoryCustomImpl(EntityManager entityManager) {
        super(Lookbook.class, entityManager);
    }



    @Override
    public PageImpl<Lookbook> findAllLookbook(Pageable pageable){
        JPAQuery<?> query = queryFactory.from(lookbook);

        final Long count = queryFactory.select(lookbook.count())
                .from(lookbook)
                .fetchOne();

        List<Lookbook> lookbookList = Objects.requireNonNull(getQuerydsl())
                .applyPagination(pageable, query)
                .select(lookbook)
                .fetch();


        return new PageImpl<>(lookbookList, pageable, count);
    }

    @Override
    public PageImpl<Lookbook> findAllLookbookByUserId(int userId, Pageable pageable) {
        JPAQuery<?> query = queryFactory.from(lookbook);
        final Long count = query.select(lookbook.count())
                .from(lookbook)
                .where(lookbook.user.userId.eq(userId))
                .fetchOne();

        List<Lookbook> lookbookList = query.select(lookbook)
                .from(lookbook)
                .where(lookbook.user.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(lookbookList, pageable, count);
    }

    @Override
    public PageImpl<Lookbook> searchLookbook(String keyword, Pageable pageable) {
        JPAQuery<?> where = queryFactory.from(QClothesLookbook.clothesLookbook)
                .leftJoin(QClothesLookbook.clothesLookbook.lookbook, lookbook)
                .leftJoin(QClothesLookbook.clothesLookbook.clothes, QClothes.clothes)
                .where(
                        QClothes.clothes.name.contains(keyword)
                                .or(QClothes.clothes.brand.contains(keyword))
                                );


        List<Lookbook> lookbookList = Objects.requireNonNull(getQuerydsl())
                .applyPagination(pageable, where)
                .select(lookbook)
                .fetch();

        long count = Objects.requireNonNull(getQuerydsl())
                .applyPagination(pageable, where)
                .select(lookbook)
                .fetchCount();

        return new PageImpl<>(lookbookList, pageable, count);
    }

    @Override
    public List<Lookbook> findTopLookbook(LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory.select(lookbook)
                .from(likes)
                .join(likes.lookbook, lookbook)
                .where(lookbook.createdAt.between(startDate, endDate))
                .groupBy(lookbook)
                .orderBy(likes.count().desc())
                .limit(15)
                .fetch();
    }

    @Override
    public List<LookBookTopResponse_v2> findTopLookbook_v2(LocalDateTime startDate, LocalDateTime endDate) {
        QLikes likes= QLikes.likes;

        return queryFactory.select(
                        Projections.constructor(
                                LookBookTopResponse_v2.class,
                                lookbook.lookbookId,
                                lookbook.user.userId,
                                lookbook.user.nickname,
                                lookbook.user.profileImg,
                                lookbook.thumbnail,
                                likes.count().intValue(),
                                lookbook.createdAt
                        )
                )
                .from(QLikes.likes)
                .join(QLikes.likes.lookbook, lookbook)
                .where(likes.updatedAt.between(startDate,endDate))
                .groupBy(lookbook)
                .orderBy(QLikes.likes.count().desc())
                .limit(20)
                .fetch();
    }


    @Override
    public List<TopLookBookDto> getTopLookBookDetailInfo(List<Integer> lookbookIds){
        return queryFactory.select(
                Projections.constructor(
                        TopLookBookDto.class,
                        lookbook.lookbookId,
                        lookbook.user.userId,
                        lookbook.user.nickname,
                        lookbook.user.profileImg,
                        lookbook.createdAt,
                        lookbook.thumbnail,
                        Expressions.constant(0),
                        lookbook.isPublic
                ))
                .from(lookbook)
                .where(lookbook.lookbookId.in(lookbookIds))
                .innerJoin(lookbook.user, QUser.user)
                .fetch();
    }

    @Override
    public PageImpl<Lookbook> searchLookbookByTag(ArrayList<String> tags, Pageable pageable) {
        JPAQuery<?> where = queryFactory.from(QTagLookbook.tagLookbook)
                .leftJoin(QTagLookbook.tagLookbook.lookbook, lookbook)
                .leftJoin(QTagLookbook.tagLookbook.tag, QTag.tag)
                .where(
                        containTags(tags)
                )
                .distinct();

        List<Lookbook> lookbookList = Objects.requireNonNull(getQuerydsl())
                .applyPagination(pageable, where)
                .select(lookbook)
                .fetch();

        long count = Objects.requireNonNull(getQuerydsl())
                .applyPagination(pageable, where)
                .select(lookbook)
                .fetchCount();
        return new PageImpl<>(lookbookList, pageable, count);
    }
    private BooleanExpression containTags(ArrayList<String> tags){
        if(tags.size() == 0) throw new TagNotExistException("검색 태그를 한 개 이상 입력해주세요.");
        String firstTag = tags.get(0);
        BooleanExpression query = QTag.tag.name.contains(firstTag);
        if(tags.size() == 1) return query;

        for(int i=1 ; i < tags.size() ; i++){
            query = query.and(QTag.tag.name.contains(tags.get(i)));
        }
        return query;
    }

}
