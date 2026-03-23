package com.cute.gawm.domain.stylelog.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStylelog is a Querydsl query type for Stylelog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStylelog extends EntityPathBase<Stylelog> {

    private static final long serialVersionUID = -422066323L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStylelog stylelog = new QStylelog("stylelog");

    public final com.cute.gawm.common.QBaseEntity _super = new com.cute.gawm.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.sql.Timestamp> date = createDateTime("date", java.sql.Timestamp.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final StringPath location = createString("location");

    public final NumberPath<Integer> stylelogId = createNumber("stylelogId", Integer.class);

    public final StringPath stylelogImg = createString("stylelogImg");

    public final NumberPath<Integer> temperature = createNumber("temperature", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.cute.gawm.domain.user.entity.QUser user;

    public final StringPath weather = createString("weather");

    public QStylelog(String variable) {
        this(Stylelog.class, forVariable(variable), INITS);
    }

    public QStylelog(Path<? extends Stylelog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStylelog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStylelog(PathMetadata metadata, PathInits inits) {
        this(Stylelog.class, metadata, inits);
    }

    public QStylelog(Class<? extends Stylelog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.cute.gawm.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

