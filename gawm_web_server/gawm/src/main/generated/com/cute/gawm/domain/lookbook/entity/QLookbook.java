package com.cute.gawm.domain.lookbook.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLookbook is a Querydsl query type for Lookbook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLookbook extends EntityPathBase<Lookbook> {

    private static final long serialVersionUID = 1174174103L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLookbook lookbook = new QLookbook("lookbook");

    public final com.cute.gawm.common.QBaseEntity _super = new com.cute.gawm.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final NumberPath<Integer> lookbookId = createNumber("lookbookId", Integer.class);

    public final StringPath thumbnail = createString("thumbnail");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.cute.gawm.domain.user.entity.QUser user;

    public final NumberPath<Integer> view = createNumber("view", Integer.class);

    public QLookbook(String variable) {
        this(Lookbook.class, forVariable(variable), INITS);
    }

    public QLookbook(Path<? extends Lookbook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLookbook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLookbook(PathMetadata metadata, PathInits inits) {
        this(Lookbook.class, metadata, inits);
    }

    public QLookbook(Class<? extends Lookbook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.cute.gawm.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

