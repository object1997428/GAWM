package com.cute.gawm.domain.tag_lookbook.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTagLookbook is a Querydsl query type for TagLookbook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTagLookbook extends EntityPathBase<TagLookbook> {

    private static final long serialVersionUID = -678118482L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTagLookbook tagLookbook = new QTagLookbook("tagLookbook");

    public final com.cute.gawm.common.QBaseEntity _super = new com.cute.gawm.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final com.cute.gawm.domain.lookbook.entity.QLookbook lookbook;

    public final com.cute.gawm.domain.tag.entity.QTag tag;

    public final NumberPath<Integer> tagLookbookId = createNumber("tagLookbookId", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTagLookbook(String variable) {
        this(TagLookbook.class, forVariable(variable), INITS);
    }

    public QTagLookbook(Path<? extends TagLookbook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTagLookbook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTagLookbook(PathMetadata metadata, PathInits inits) {
        this(TagLookbook.class, metadata, inits);
    }

    public QTagLookbook(Class<? extends TagLookbook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lookbook = inits.isInitialized("lookbook") ? new com.cute.gawm.domain.lookbook.entity.QLookbook(forProperty("lookbook"), inits.get("lookbook")) : null;
        this.tag = inits.isInitialized("tag") ? new com.cute.gawm.domain.tag.entity.QTag(forProperty("tag")) : null;
    }

}

