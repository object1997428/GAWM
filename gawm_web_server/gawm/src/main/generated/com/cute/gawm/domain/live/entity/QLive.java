package com.cute.gawm.domain.live.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLive is a Querydsl query type for Live
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLive extends EntityPathBase<Live> {

    private static final long serialVersionUID = -1990887969L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLive live = new QLive("live");

    public final com.cute.gawm.common.QBaseEntity _super = new com.cute.gawm.common.QBaseEntity(this);

    public final StringPath afterImg = createString("afterImg");

    public final StringPath beforeImg = createString("beforeImg");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final NumberPath<Integer> liveId = createNumber("liveId", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final StringPath session = createString("session");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.cute.gawm.domain.user.entity.QUser user;

    public QLive(String variable) {
        this(Live.class, forVariable(variable), INITS);
    }

    public QLive(Path<? extends Live> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLive(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLive(PathMetadata metadata, PathInits inits) {
        this(Live.class, metadata, inits);
    }

    public QLive(Class<? extends Live> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.cute.gawm.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

