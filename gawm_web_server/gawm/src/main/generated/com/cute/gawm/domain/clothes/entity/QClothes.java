package com.cute.gawm.domain.clothes.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClothes is a Querydsl query type for Clothes
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClothes extends EntityPathBase<Clothes> {

    private static final long serialVersionUID = 1089384365L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClothes clothes = new QClothes("clothes");

    public final com.cute.gawm.common.QBaseEntity _super = new com.cute.gawm.common.QBaseEntity(this);

    public final StringPath brand = createString("brand");

    public final NumberPath<Integer> clothesId = createNumber("clothesId", Integer.class);

    public final StringPath clothesImg = createString("clothesImg");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final StringPath mCategory = createString("mCategory");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> orderNum = createNumber("orderNum", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.cute.gawm.domain.user.entity.QUser user;

    public QClothes(String variable) {
        this(Clothes.class, forVariable(variable), INITS);
    }

    public QClothes(Path<? extends Clothes> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClothes(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClothes(PathMetadata metadata, PathInits inits) {
        this(Clothes.class, metadata, inits);
    }

    public QClothes(Class<? extends Clothes> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.cute.gawm.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

